package utils

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpHeader.ParsingResult
import akka.http.scaladsl.model._
import org.jsoup.Jsoup

import scala.concurrent.Future
import scala.jdk.CollectionConverters._
import service.ServiceBindings._
import utils.ColumnUtils.{FloatType, StringType}
import utils.DataFeeder._

import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.language.postfixOps
import scala.util.Try


trait DataFeeder {
  def getRealtime(symbol: String): Future[Map[String, Any]] =
    getHtmlText(
      REALTIME_PATH,
      getRealtimeQuery(symbol)
    ).map(getMap(_))
  def getDaily(symbol: String): Future[Seq[Table]] =
    getHtmlText(
      HISTORY_PATH,
      getHistoryQuery(symbol)
    ).map(getTable(_))
}

private[utils] object DataFeeder {

  val HOSTNAME = "www.settrade.com"
  val REALTIME_PATH = "/C04_01_stock_quote_p1.jsp"
  val HISTORY_PATH = "/C04_02_stock_historical_p1.jsp"
  val HEADERS: Seq[HttpHeader] = Map("cookie" -> "IPO_Language=English")
    .foldLeft(Seq.empty[HttpHeader]) {
      case (seq, (key, value)) => HttpHeader.parse(key, value) match {
        case ParsingResult.Ok(h, _) => seq :+ h
        case _ => seq
      }
    }
  val ENTITY_TIMEOUT: FiniteDuration = 5 second

  def getRealtimeQuery(symbol: String) = s"txtSymbol=${symbol}"

  def getHistoryQuery(symbol: String) = s"txtSymbol=${symbol}&max=120&offset=0"

  def getHtmlText(path: String, query: String): Future[String] = {
    val request = HttpRequest(
      uri = Uri.from(
        scheme = "https", host = HOSTNAME, path = path, queryString = Some(query)
      ), headers = HEADERS
    )
    val responseFuture = Http().singleRequest(request)
    for {
      response <- responseFuture
        strict <- response.entity.toStrict(ENTITY_TIMEOUT)
    } yield {
      strict.data.decodeString("UTF-8")
    }
  }

  def getMap(htmlText: String, nthTables: Set[Int]= Set.empty[Int]): Map[String, Any] = {
    val document = Jsoup.parse(htmlText)
    val each = document.select("table.table tr")
    Range(0, each.size)
      .foldLeft(Seq.empty[Seq[String]]) { case (rSeq, idx) if nthTables.isEmpty || nthTables.contains(idx)=> rSeq :+ each
        .get(idx)
        .select("td")
        .eachText()
        .asScala
        .toSeq
      }.map {
      case key :: value :: _ => (Dict.KEYS_DICT.getOrElse(key, key), Try(value.asInstanceOf[Float]).getOrElse(value))
    }.toMap

  }

  def getTable(htmlText: String, nthTables: Set[Int] = Set.empty[Int]): Seq[Table] = {
    val document = Jsoup.parse(htmlText)
    val t = document.select("table.table")
    Range(0, t.size).foldLeft(Seq.empty[Table]) {
      case (tSeq, tableIdx) if nthTables.isEmpty || nthTables.contains(tableIdx) =>
        val table = t.get(tableIdx)
        val columnNames = table.select("th").eachText().asScala.toSeq
        val rawRows = table.select("tr")
        val rows = Range(0, rawRows.size).foldLeft(Seq.empty[Seq[String]]) { case (rSeq, idx) =>
          val row = rawRows
            .get(idx)
            .select("td")
            .eachText()
            .asScala
            .toSeq
          if (row.nonEmpty) rSeq :+ row else rSeq
        }
        tSeq :+ Table(ColumnUtils.getColumns(columnNames, rows, Map("Date" -> StringType), FloatType))

    }
  }
}
