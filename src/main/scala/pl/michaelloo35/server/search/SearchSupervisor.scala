package pl.michaelloo35.server.search

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorRef, OneForOneStrategy, Props, SupervisorStrategy}
import akka.event.Logging
import akka.routing.FromConfig
import pl.michaelloo35.server.search.model.{WorkerSearchRequest, WorkerSearchResponse, WorkerSearchResponseFound, WorkerSearchResponseNotFound}
import pl.michaelloo35._

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._

class SearchSupervisor extends Actor {
  val log = Logging(context.system, this)
  val requestCounter = new AtomicInteger(0)

  // helpful Array to eliminate duplicates Tri-state because if we'll get NotFound then we have to wait for results from second database
  val completedRequests: ArrayBuffer[State] = new ArrayBuffer[State]()

  // pool of db1 workers
  var db1Router: ActorRef = _

  // pool of db2 workers
  var db2Router: ActorRef = _

  override def receive: Receive = {
    case SearchRequest(title) =>
      // id to eliminate duplicates
      val id = requestCounter.getAndIncrement()
      completedRequests.insert(id, NotReplied)

      // search in db1
      db1Router ! WorkerSearchRequest(title, sender, id)

      // search in db2
      db2Router ! WorkerSearchRequest(title, sender, id)

    case res: WorkerSearchResponse =>
      // check if duplicate

      completedRequests(res.id) match {
        case NotReplied =>
          res match {
            case WorkerSearchResponseFound(title, price, requestClient, _) =>
              completedRequests(res.id) = Replied
              requestClient ! SearchSuccess(title, price)

            case WorkerSearchResponseNotFound(_, _, _) =>
              completedRequests(res.id) = Waiting

          }

        case Waiting =>
          res match {
            case WorkerSearchResponseFound(title, price, requestClient, _) =>
              completedRequests(res.id) = Replied
              requestClient ! SearchSuccess(title, price)

            case WorkerSearchResponseNotFound(title, requestClient, _) =>
              completedRequests(res.id) = Replied
              requestClient ! SearchFailure(title, "Book not found")
          }

        case Replied => Unit
      }
  }

  override def preStart(): Unit = {
    db1Router = context.actorOf(FromConfig.props(Props[Db1Worker]), "db1_router")
    db2Router = context.actorOf(FromConfig.props(Props[Db2Worker]), "db2_router")
  }

  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: Exception ⇒ Restart
    }

}
