package pl.michaelloo35.server.search

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorRef, OneForOneStrategy, Props, SupervisorStrategy}
import akka.event.Logging
import akka.routing.FromConfig
import pl.michaelloo35.server.search.model.{WorkerSearchRequest, WorkerSearchResponse, WorkerSearchResponseFound, WorkerSearchResponseNotFound}
import pl.michaelloo35.{SearchFailure, SearchRequest, SearchSuccess}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._

class SearchSupervisor extends Actor {
  val log = Logging(context.system, this)
  val requestCounter = new AtomicInteger(0)

  // helpful Array to eliminate duplicates
  // TODO Change into Tri-state array
  val completedRequests: ArrayBuffer[Boolean] = new ArrayBuffer[Boolean]()

  // pool of db1 workers
  var db1Router: ActorRef = _

  // pool of db2 workers
  var db2Router: ActorRef = _

  override def receive: Receive = {
    case SearchRequest(title) =>
      // id to eliminate duplicates
      val id = requestCounter.getAndIncrement()
      completedRequests.insert(id, false)

      // search in db1
      db1Router ! WorkerSearchRequest(title, sender, id)

      // search in db2
      db2Router ! WorkerSearchRequest(title, sender, id)

    case res: WorkerSearchResponse =>
      // check if duplicate
      if (!completedRequests(res.id)) {
        completedRequests(res.id) = true

        // handle worker response
        res match {
          case WorkerSearchResponseFound(title, price, requestClient, _) =>
            requestClient ! SearchSuccess(title, price)

          case WorkerSearchResponseNotFound(title, requestClient, _) =>
            requestClient ! SearchFailure(title, "Book not found")
        }
      }

      // else do nothing
      else
        Unit


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
