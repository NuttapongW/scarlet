package utils

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpHeader.ParsingResult
import akka.http.scaladsl.model._
import org.jsoup.Jsoup

import scala.concurrent.Future
import scala.jdk.CollectionConverters._
import Bindings._
import utils.ColumnUtils.{FloatType, StringType}

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.Try

object Data {

  val HOSTNAME = "www.settrade.com"
  val REALTIME_PATH = "/C04_01_stock_quote_p1.jsp"
  val HISTORY_PATH = "/C04_02_stock_historical_p1.jsp"
  val HEADERS: Seq[HttpHeader] = Map("cookie" -> "IPO_Language=English")
    .foldLeft(Seq.empty[HttpHeader]) { case (seq, (key, value)) => HttpHeader.parse(key, value) match {
      case ParsingResult.Ok(h, _) => seq :+ h
      case _ => seq
    }
    }

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
        strict <- response.entity.toStrict(1 second)
    } yield {
      strict.data.decodeString("UTF-8")
    }
  }

  def getMap(htmlText: String) = {
    val document = Jsoup.parse(htmlText)
    val each = document.select("table.table tr")
    Range(0, each.size)
      .foldLeft(Seq.empty[Seq[String]]) { case (rSeq, idx) => rSeq :+ each
        .get(idx)
        .select("td")
        .eachText()
        .asScala
        .toSeq
      }.map {
      case key :: value :: _ => (key, Try(value.asInstanceOf[Float]).getOrElse(value))
    }.toMap

  }

  def getTable(htmlText: String) = {
    val document = Jsoup.parse(htmlText)
    val t = document.select("table.table")
    Range(0, t.size).foldLeft(Seq.empty[Table]) {
      case (tSeq, tableIdx) => {
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
        tSeq :+ Table(ColumnUtils.getColumns(Some(columnNames), rows, Map("Date" -> StringType), FloatType))
      }
    }
  }

  def transformRealtimeMap(map: Map[String, Any]) = {
  }
}
