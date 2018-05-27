package pl.michaelloo35.server.order.model

import akka.actor.ActorRef

case class WorkerOrderRequest(title: String, price: Double, requestClient: ActorRef)
