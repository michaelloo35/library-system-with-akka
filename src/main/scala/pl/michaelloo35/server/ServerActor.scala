package pl.michaelloo35.server

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorRef, OneForOneStrategy, Props, SupervisorStrategy}
import akka.event.Logging
import scala.concurrent.duration._
import pl.michaelloo35.server.order.OrderSupervisor
import pl.michaelloo35.server.search.SearchSupervisor
import pl.michaelloo35.server.stream.StreamSupervisor
import pl.michaelloo35.{OrderRequest, SearchRequest, StreamRequest}


class ServerActor extends Actor {
  val log = Logging(context.system, this)
  var searchSupervisor: ActorRef = _
  var orderSupervisor: ActorRef = _
  var streamSupervisor: ActorRef = _

  override def receive: Receive = {
    case r: SearchRequest => searchSupervisor.tell(r, sender)
    case r: OrderRequest => orderSupervisor.tell(r, sender)
    case r: StreamRequest => streamSupervisor.tell(r, sender)
    case _ ⇒ log.info("received unknown message")

  }

  override def preStart(): Unit = {
    searchSupervisor = context.actorOf(Props[SearchSupervisor], "search_supervisor")
    orderSupervisor = context.actorOf(Props[OrderSupervisor], "order_supervisor")
    streamSupervisor = context.actorOf(Props[StreamSupervisor], "stream_supervisor")
  }


  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: Exception ⇒ Restart
    }

}
