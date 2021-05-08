package org.lyghtning

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpHeader.ParsingResult
import akka.http.scaladsl.model.{HttpHeader, HttpRequest, Uri}
import org.jsoup.Jsoup

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import Bindings._

import scala.jdk.CollectionConverters._

object Utils {

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

  def getRealtimeQuery(symbol: String) = s"txtSymbol=${symbol}"
  def getHistoryQuery(symbol: String) = s"txtSymbol=${symbol}&max=120&offset=0"

  def getHtmlText(path: String, query: String): Future[String] = {
    val request = HttpRequest(
      uri = Uri.from(
        scheme = "https",
        host = HOSTNAME,
        path = path,
        queryString = Some(query)
      ),
      headers = HEADERS
    )
    val responseFuture = Http().singleRequest(request)
    for {
      response <- responseFuture
        strict <- response.entity.toStrict(1 millis)
    } yield strict.data.decodeString("UTF-8")
  }

  def getTables(htmlText: String) = {
    val document = Jsoup.parse(htmlText)
    val t = document.select("table.table")
    Range(0, t.size).foldLeft(Seq.empty[Seq[Seq[String]]]) {
      case (tSeq, tableIdx) => {
        val rows = t.get(tableIdx).select("tr")
        tSeq :+ Range(0, rows.size).foldLeft(Seq.empty[Seq[String]]) {
            case (rSeq, idx) => rSeq :+ rows.get(idx).select("td").eachText().asScala.toSeq
        }
      }
    }
  }

}
