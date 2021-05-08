package org.lyghtning

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors

object Bindings {
  implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
  implicit val executionContext = system.executionContext
}
