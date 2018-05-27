package pl.michaelloo35.server.search.model

import akka.actor.ActorRef

case class WorkerSearchResponseNotFound(title: String, requestClient: ActorRef, id: Int) extends WorkerSearchResponse