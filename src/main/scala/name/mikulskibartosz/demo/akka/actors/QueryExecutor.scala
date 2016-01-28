package name.mikulskibartosz.demo.akka.actors

import akka.actor.Actor
import name.mikulskibartosz.demo.akka.messages.{ExecuteQuery, ServiceUnavailable}
import name.mikulskibartosz.demo.akka.service.Service

import scala.util.{Failure, Success}

/**
  * Sends a query to the service.
  *
  * The actor fails if the service has thrown an exception.
  */
class QueryExecutor(private val service: Service) extends Actor {
  override def receive = {
    case ExecuteQuery => service.generate() match {
      case Success(number) => sender() ! number
      case Failure(ex) =>
        sender() ! akka.actor.Status.Failure(ex)
        val o = sender()
        context.parent ! ServiceUnavailable
        throw ex
    }
  }
}
