package pl.michaelloo35.server

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import pl.michaelloo35.SearchRequest
import pl.michaelloo35.server.search.SearchSupervisor


class ServerActor extends Actor {
  val log = Logging(context.system, this)
  var searchSupervisor: ActorRef = _

  override def receive: Receive = {
    case r: SearchRequest => searchSupervisor.tell(r, sender)
    case "order" ⇒ log.info("received order")
    case "stream" ⇒ log.info("received stream")
    case _ ⇒ log.info("received unknown message")

  }

  override def preStart(): Unit = {
    searchSupervisor = context.actorOf(Props.create(classOf[SearchSupervisor]), "search_supervisor")

  }
}
