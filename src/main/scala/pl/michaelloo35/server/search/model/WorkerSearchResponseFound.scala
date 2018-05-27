package pl.michaelloo35.server.search.model

import akka.actor.ActorRef

case class WorkerSearchResponseFound(title: String, price: Double, requestClient: ActorRef, id: Int) extends WorkerSearchResponse
