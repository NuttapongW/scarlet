package org.lyghtning
package actor

import akka.actor.Actor
import message.Messages.GetRealtimeInfo

import Bindings._
import Utils._

import scala.language.postfixOps

class Crawler extends Actor {
  override def receive: Receive = {
    case GetRealtimeInfo(symbol: String) => {
      val tables = getHtmlText(REALTIME_PATH, getRealtimeQuery(symbol)).map(getTables)
    }
  }
}
