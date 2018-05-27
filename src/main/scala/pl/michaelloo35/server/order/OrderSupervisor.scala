package pl.michaelloo35.server.order

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorRef, ActorSelection, OneForOneStrategy, Props, SupervisorStrategy}
import akka.event.Logging
import akka.routing.FromConfig
import akka.pattern.ask
import akka.util.Timeout
import pl.michaelloo35._
import pl.michaelloo35.server.order.model.{WorkerOrderRequest, WorkerOrderResponse, WorkerOrderResponseSuccess}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class OrderSupervisor extends Actor {
  private implicit val executionContext: ExecutionContextExecutor = context.system.dispatcher
  private implicit val duration: Timeout = 15 seconds
  val log = Logging(context.system, this)
  val searchSupervisorPathLocal = "akka.tcp://server_system@127.0.0.1:3552/user/server/search_supervisor"
  var searchSupervisor: ActorSelection = _
  var orderRouter: ActorRef = _


  override def receive: Receive = {
    // ask search_supervisor if the book is available record appropriate callbacks
    case OrderRequest(title) =>
      // GOTTA SAVE THIS SENDER
      val s = sender
      (searchSupervisor ? SearchRequest(title)).onComplete {
        case Success(result) =>
          result match {
            case SearchSuccess(_, price) => orderRouter ! WorkerOrderRequest(title, price, s)
            case SearchFailure(_, _) => s ! OrderResponse("Order failed - Book " + title + " could not be found in system")
          }

        case Failure(_) =>
          s ! OrderResponse("Order failed - system error")
      }

    case r: WorkerOrderResponse =>
      r match {
        case WorkerOrderResponseSuccess(title, price, requestClient) =>
          requestClient ! OrderResponse("Order success - " + title + " in price of " + price + " placed")
      }


  }

  override def preStart(): Unit = {
    orderRouter = context.actorOf(FromConfig.props(Props[OrderWorker]), "order_router")
    searchSupervisor = context.actorSelection(searchSupervisorPathLocal)
  }

  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: Exception ⇒ Restart
    }

}
