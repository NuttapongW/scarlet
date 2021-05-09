package actor

import akka.actor.Actor
import message.Messages.{GetDailyInfo, GetRealtimeInfo}
import utils.Bindings._

import utils.Data._

import scala.language.postfixOps

class Crawler extends Actor {
  override def receive: Receive = {
    case GetRealtimeInfo(symbol: String) => {
      val map = getHtmlText(REALTIME_PATH, getRealtimeQuery(symbol)).map(getMap)
    }
    case GetDailyInfo(symbol: String) =>
      val table = getHtmlText(HISTORY_PATH, getHistoryQuery(symbol)).map(getTable)
  }
}
