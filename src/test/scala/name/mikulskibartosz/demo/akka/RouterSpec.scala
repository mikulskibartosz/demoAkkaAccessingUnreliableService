package name.mikulskibartosz.demo.akka

import java.util.concurrent.TimeUnit

import akka.actor.Props
import akka.pattern.ask
import akka.testkit.{TestActorRef, TestProbe}
import name.mikulskibartosz.demo.akka.actors.AbstractActorSpec
import name.mikulskibartosz.demo.akka.messages.{ExecuteQuery, ServiceUnavailable}
import name.mikulskibartosz.demo.akka.router.MessageRouter

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

/**
  * Tests whether the router passes a message to its child and returns the value.
  */
class RouterSpec extends AbstractActorSpec {
  "A router" should {
    "return a response" in {
      //given
      val service = createServiceReturning(Success(2))
      val objectUnderTest = TestActorRef(new MessageRouter(service))

      //when
      val response = objectUnderTest ? ExecuteQuery

      //then
      Await.result(response, Duration(3, TimeUnit.SECONDS))
      assert(response.value.get == Success(2))
    }
  }

  it should {
    "notify the sender if the service has thrown an exception" in {
      //given
      val expectedResult: Failure[Int] = Failure(new RuntimeException())
      val service = createServiceReturning(expectedResult)
      val objectUnderTest = TestActorRef(new MessageRouter(service))

      //when
      val response = objectUnderTest ? ExecuteQuery
      //then
      Await.result(response, Duration(3, TimeUnit.SECONDS))
      assert(response.value.get.failed.get == expectedResult.failed.get)
    }
  }

  it should {
    "pass a message to its parent if the service has thrown an exception" in {
      val probe = TestProbe()
      val expectedResult: Failure[Int] = Failure(new RuntimeException())
      val service = createServiceReturning(expectedResult)
      val objectUnderTest = TestActorRef[MessageRouter](Props(new MessageRouter(service)), probe.ref)

      //when
      val response = objectUnderTest ? ExecuteQuery

      //then
      probe.expectMsg(ServiceUnavailable)
    }
  }
}
