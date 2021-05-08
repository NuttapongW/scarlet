package org.lyghtning
package message

import java.time.ZonedDateTime

object Messages {

  case class GetProperties(stock: String)
  case class GetDailyInfo(stock: String)
  case class GetRealtimeInfo(stock: String)
  case class GetFinancial(stock: String)

  case class StockProperties(
    stock: String,
    business: String,
    industry: String,
    ipoDate: ZonedDateTime,
    foreignP: Float,
    freeFloatP: Float
  )
  case class StockDailyInfo(
    stock: String,
    open: Float,
    close: Float,
    delta: Float,
    avg: Float,
    max: Float,
    min: Float,
    volume: Long,
    value: Float
  )
  case class StockRealtimeInfo(stock: String, timeStamp: ZonedDateTime, price: Float, volume: Long, value: Float)

}
