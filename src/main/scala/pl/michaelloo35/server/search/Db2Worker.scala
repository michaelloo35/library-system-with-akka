package pl.michaelloo35.server.search

import akka.actor.Actor

class Db2Worker extends DbWorker with Actor {
  override val databasePath = "/database/db2"
}
