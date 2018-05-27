package pl.michaelloo35.server.search.dbmodel

import akka.actor.ActorRef

case class DbSearchResponseFound(title: String, price: Double, requestClient: ActorRef,id: Int) extends DbSearchResponse
