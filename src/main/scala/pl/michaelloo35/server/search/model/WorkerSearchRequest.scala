package pl.michaelloo35.server.search.model

import akka.actor.ActorRef

case class WorkerSearchRequest(title: String, requestClient: ActorRef, id: Int)
