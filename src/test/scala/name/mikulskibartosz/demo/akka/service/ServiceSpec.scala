package name.mikulskibartosz.demo.akka.service

import org.mockito.Mockito._
import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar

import scala.util.Success

/**
  * The service should return a random value or throw an exception if the value is a negative number.
  */
class ServiceSpec extends FlatSpec with MockitoSugar {
  "A service" should "return a number" in {
    //given
    val number = 2
    val mock = createMockReturning(number)

    val objectUnderTest = new Service(mock)

    //when
    val result = objectUnderTest.generate()

    //then
    assert(result == Success(number))
  }

  it should "throw an exception" in {
    //given
    val number = -2
    val mock = createMockReturning(number)

    val objectUnderTest = new Service(mock)

    //when
    val result = objectUnderTest.generate()

    //then
    assert(result.isFailure)
  }

  private def createMockReturning(value: Int) = {
    val number = value + 100 //the service subtracts 100
    val randomGenerator = mock[java.util.Random]
    when(randomGenerator.nextInt(200)).thenReturn(number)

    randomGenerator
  }
}
