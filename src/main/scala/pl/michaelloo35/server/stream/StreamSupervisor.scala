package pl.michaelloo35.server.stream

import java.io.File

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorSelection, OneForOneStrategy, SupervisorStrategy}
import akka.event.Logging
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.Timeout
import pl.michaelloo35._
import pl.michaelloo35.server.Server

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

class StreamSupervisor extends Actor {
  private implicit val executionContext: ExecutionContextExecutor = context.system.dispatcher
  private implicit val duration: Timeout = 15 seconds
  private implicit val materializer: ActorMaterializer = ActorMaterializer()

  val log = Logging(context.system, this)
  val searchSupervisorPathLocal = "akka.tcp://server_system@127.0.0.1:3552/user/server/search_supervisor"
  var searchSupervisor: ActorSelection = _

  override def receive: Receive = {
    case StreamRequest(title) =>
      val s = sender
      for (future <- searchSupervisor ? SearchRequest(title)) yield {
        future match {
          case SearchFailure(_, _) => s ! StreamFailure("Stream failed - Book " + title + " could not be found in system")
          case SearchSuccess(_, _) =>
            Source
              .fromIterator(
                scala.io.Source
                  .fromFile(new File(Server.getClass.getResource("/books/" + title).toURI))
                  .getLines
              )
              .map(line => StreamReply(line))
              .throttle(1, 1.second)
              .runWith(Sink.actorRef(s, StreamReply(">>>EOF<<<")))

        }
      }
  }


  override def preStart(): Unit = {
    searchSupervisor = context.actorSelection(searchSupervisorPathLocal)
  }

  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: Exception ⇒ Restart
    }

}
