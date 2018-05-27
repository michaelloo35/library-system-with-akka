package pl.michaelloo35.server

import java.io.File

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object Server extends App {

  val PORT = 3552
  val configFile = new File(getClass.getResource("/server/remote.conf").toURI)
  val config = ConfigFactory.parseFile(configFile)

  val system = ActorSystem.create("server_system", config)
  val remote = system.actorOf(Props.create(classOf[ServerActor]), "server")

}
