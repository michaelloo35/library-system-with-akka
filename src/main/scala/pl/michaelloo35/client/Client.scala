package pl.michaelloo35.client

import java.io.{BufferedReader, File, InputStreamReader}

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object Client extends App {

  val configFile = new File(getClass.getResource("/client/remote.conf").toURI)
  val config = ConfigFactory.parseFile(configFile)


  val system = ActorSystem.create("client_system", config)
  val clientActor = system.actorOf(Props.create(classOf[ClientActor]), "client")

  val br = new BufferedReader(new InputStreamReader(System.in))
  while (true) {
    val line = br.readLine
    clientActor ! line
  }

  // finish
  system.terminate
}
