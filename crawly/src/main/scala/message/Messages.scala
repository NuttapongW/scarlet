package message

import java.time.ZonedDateTime

object Messages {

  case object GetStockList
  case class GetProperties(symbol: String)
  case class GetDailyInfo(symbol: String)
  case class GetRealtimeInfo(symbol: String)
  case class GetFinancial(symbol: String)

  case class StockProperties(
    symbol: String,
    business: String,
    industry: String,
    ipoDate: ZonedDateTime,
    foreignP: Float,
    freeFloatP: Float
  )
  case class StockDailyInfo(
    symbol: String,
    open: Float,
    close: Float,
    delta: Float,
    avg: Float,
    max: Float,
    min: Float,
    volume: Long,
    value: Float
  )
  case class StockRealtimeInfo(
    symbol: String,
    timeStamp: ZonedDateTime,
    bid: Float,
    offer: Float,
    bidVolume: Long,
    offerVolume: Long,
    volume: Long,
    value: Float
  )

}
