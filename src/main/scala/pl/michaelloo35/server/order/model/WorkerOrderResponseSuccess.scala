package pl.michaelloo35.server.order.model

import akka.actor.ActorRef

case class WorkerOrderResponseSuccess(title: String, price: Double, requestClient: ActorRef) extends WorkerOrderResponse
