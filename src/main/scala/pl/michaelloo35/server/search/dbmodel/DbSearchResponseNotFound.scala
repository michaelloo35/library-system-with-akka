package pl.michaelloo35.server.search.dbmodel

import akka.actor.ActorRef

case class DbSearchResponseNotFound(title: String, requestClient: ActorRef, id: Int) extends DbSearchResponse