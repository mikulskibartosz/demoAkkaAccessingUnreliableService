package name.mikulskibartosz.demo.akka

/**
  * Messages supported by the actor system.
  */
package object messages {

  /**
    * Indicates that the application should send a query to the service.
    */
  case object ExecuteQuery

  /**
    * Indicates that the service returned error or is not responding.
    */
  case object ServiceUnavailable
}
