package name.mikulskibartosz.demo.akka.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, DefaultTimeout, TestKit}
import name.mikulskibartosz.demo.akka.service.Service
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.util.Try

/**
  * A base specification used to test actors.
  *
  * Note that the class automatically closes the actor system.
  */
abstract class AbstractActorSpec extends TestKit(ActorSystem()) with DefaultTimeout with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll with MockitoSugar {
  override def afterAll() = {
    super.afterAll()
    shutdown()
  }

  def createServiceReturning(value: Try[Int]) = {
    val service = mock[Service]
    when(service.generate()).thenReturn(value)
    service
  }
}
