package utils

trait DataFetcher {
  def fetchRealtime(map: Map[String, Any]): Unit = {}
  def fetchDaily(table: Table): Unit = {}
}
