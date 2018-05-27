package pl.michaelloo35.server.order

import java.io.{File, FileOutputStream, PrintWriter}
import java.nio.file.Paths
import java.util.Calendar

import akka.actor.Actor
import akka.util.Timeout
import pl.michaelloo35.server.order.model.{WorkerOrderRequest, WorkerOrderResponseSuccess}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

class OrderWorker extends Actor {
  private implicit val timeout: Timeout = 5 seconds
  private implicit val executionContext: ExecutionContextExecutor = context.system.dispatcher
  val ordersPath = "./src/main/resources/database/orders"

  override def receive: Receive = {
    case WorkerOrderRequest(title, price, requestClient) =>

      // bad synchronization ofc.. realized too late to change
      this.getClass.synchronized {
        val pw = new PrintWriter(new FileOutputStream(new File(Paths.get(ordersPath).toUri),true))
        pw.append(Calendar.getInstance().getTime.toString + " title " + title + " price " + price + "\n")
        sender ! WorkerOrderResponseSuccess(title, price, requestClient)
        pw.close()
      }
  }


}
