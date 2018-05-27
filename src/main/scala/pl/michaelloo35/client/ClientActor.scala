package pl.michaelloo35.client

import akka.actor.{Actor, ActorSelection}
import akka.event.Logging
import pl.michaelloo35.{SearchFailure, SearchRequest, SearchSuccess}

class ClientActor extends Actor {
  val log = Logging(context.system, this)
  val serverPath = "akka.tcp://server_system@127.0.0.1:3552/user/server"
  var remoteServerActor: ActorSelection = _

  override def receive: Receive = {
    case s: String =>
      if (s.startsWith("title:")) {
        remoteServerActor ! SearchRequest(s.split(":")(1))
      }
      else
        println("Unknown message string")

    case r: SearchSuccess => println(r.title + " price is " + r.price)
    case r: SearchFailure => println(r.reason)
    case _ => println("Unknown message")
  }

  override def preStart(): Unit = {
    remoteServerActor = context.actorSelection(serverPath)
  }
}
