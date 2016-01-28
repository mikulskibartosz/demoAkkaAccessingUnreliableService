package name.mikulskibartosz.demo.akka.service

import scala.util.{Try, Random}

/**
  * A service that returns a random number or throws an exception if the number is negative.
  */
class Service(private val generator: Random = new Random()) {
  def generate() = Try {
    val number = generator.nextInt(200) - 100

    if(number >= 0) number else throw new RuntimeException(s"Invalid number $number")
  }
}
