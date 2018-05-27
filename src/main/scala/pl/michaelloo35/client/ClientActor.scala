package pl.michaelloo35.client

import akka.actor.{Actor, ActorSelection}
import akka.event.Logging
import pl.michaelloo35._

class ClientActor extends Actor {
  val log = Logging(context.system, this)
  val serverPath = "akka.tcp://server_system@127.0.0.1:3552/user/server"
  var remoteServerActor: ActorSelection = _

  override def receive: Receive = {
    case s: String if s.startsWith("search:") =>
      remoteServerActor ! SearchRequest(s.split(":")(1))
    case s: String if s.startsWith("order:") =>
      remoteServerActor ! OrderRequest(s.split(":")(1))
    case s: String if s.startsWith("stream:") =>
      remoteServerActor ! StreamRequest(s.split(":")(1))

    case r: SearchSuccess => println(r.title + " price is " + r.price)
    case r: SearchFailure => println(r.reason)
    case r: OrderResponse => println(r.message)
    case r: StreamFailure => println(r.reason)
    case r: StreamReply => println(r.line)
    case _ => println("Unknown message")
  }

  override def preStart(): Unit = {
    remoteServerActor = context.actorSelection(serverPath)
  }
}
