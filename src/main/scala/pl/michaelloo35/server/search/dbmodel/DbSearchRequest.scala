package pl.michaelloo35.server.search.dbmodel

import akka.actor.ActorRef

case class DbSearchRequest(title: String, requestClient: ActorRef, id: Int)
