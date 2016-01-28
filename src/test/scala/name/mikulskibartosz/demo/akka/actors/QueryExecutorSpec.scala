package name.mikulskibartosz.demo.akka.actors

import akka.actor.Props
import akka.actor.SupervisorStrategy.Restart
import akka.pattern.ask
import akka.testkit.{TestActorRef, TestProbe}
import name.mikulskibartosz.demo.akka.messages.{ExecuteQuery, ServiceUnavailable}

import scala.util.{Failure, Success}

/**
  * Verifies that the query executor returns a service response or fails if the service has thrown an exception.
  */
class QueryExecutorSpec extends AbstractActorSpec {
  "The actor" should {
    "return the service response" in {
      //given
      val service = createServiceReturning(Success(2))
      val objectUnderTest = TestActorRef(new QueryExecutor(service))

      //when
      val response = objectUnderTest ? ExecuteQuery

      //then
      assert(response.value.get == Success(2))
    }
  }

  it should {
    "notify the sender if the service has thrown an exception" in {
      //given
      val expectedResult: Failure[Int] = Failure(new RuntimeException())
      val service = createServiceReturning(expectedResult)
      val objectUnderTest = TestActorRef(new QueryExecutor(service))

      //when
      val response = objectUnderTest ? ExecuteQuery

      //then
      assert(response.value.get.failed.get == expectedResult.failed.get)
    }
  }

  it should {
    "notify the parent if the service has thrown an exception" in {
      val probe = TestProbe()
      val expectedResult: Failure[Int] = Failure(new RuntimeException())
      val service = createServiceReturning(expectedResult)
      val objectUnderTest = TestActorRef[QueryExecutor](Props(new QueryExecutor(service)), probe.ref)

      //when
      val response = objectUnderTest ? ExecuteQuery

      //then
      probe.expectMsg(ServiceUnavailable)
    }
  }

  it should {
    "restart itself if the service has thrown an exception" in {
      //given
      val objectUnderTest = TestActorRef(new QueryExecutor(null))

      //when
      val decision = objectUnderTest.underlyingActor.supervisorStrategy.decider(new RuntimeException())

      //then
      assert(decision == Restart)
    }
  }
}
