package pl.michaelloo35.server.search

import java.io.File

import akka.actor.Actor
import pl.michaelloo35.server.Server
import pl.michaelloo35.server.search.dbmodel.{DbSearchRequest, DbSearchResponseFound, DbSearchResponseNotFound}

import scala.io.Source

abstract class DbWorker extends Actor {

  val databasePath: String = ""

  override def receive: Receive = {
    case DbSearchRequest(title, requestClient, id) =>
      Source
        .fromFile(new File(Server.getClass.getResource(databasePath).toURI))
        .getLines
        .find(s => s.split(":")(0) == title) match {
        case Some(book) => sender ! DbSearchResponseFound(title, book.split(":")(1).toDouble, requestClient, id)
        case None => sender ! DbSearchResponseNotFound(title, requestClient, id)
      }
  }

}
