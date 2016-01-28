package name.mikulskibartosz.demo.akka.router

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{OneForOneStrategy, Actor, Props}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import name.mikulskibartosz.demo.akka.actors.QueryExecutor
import name.mikulskibartosz.demo.akka.messages.{ExecuteQuery, ServiceUnavailable}
import name.mikulskibartosz.demo.akka.service.Service

/**
  * Creates 5 query executors and routes messages to them.
  *
  */
class MessageRouter(private val service: Service) extends Actor {
  var router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(Props(new QueryExecutor(service)))
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  override def supervisorStrategy = OneForOneStrategy() {
    case _ => Restart
  }

  def receive = {
    case ExecuteQuery => router.route(ExecuteQuery, sender())
    case ServiceUnavailable => context.parent ! ServiceUnavailable
  }
}
