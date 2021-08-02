package model

import akka.actor.{Actor, ActorLogging}
import message.Messages.{GetDailyInfo, GetRealtimeInfo}
import service.ServiceBindings._
import utils.{DataFeeder, DataFetcher}

import scala.language.postfixOps
import scala.util.Success

class Worker extends Actor with ActorLogging with DataFeeder with DataFetcher {
  override def receive: Receive = {
    case GetRealtimeInfo(symbol: String) =>
      val map = getRealtime(symbol)
      // TODO: add implementation

    case GetDailyInfo(symbol: String) =>
      val table = getDaily(symbol)
      // TODO: add implementation
  }
}
