package utils

class Table(val data: Map[String, Column[Any]]) {
  val records: Int = data.foldLeft(-1) {
    case (records, (name, column)) =>
      assert(records < 0 || records == column.records, s"Column[${name}] size mismatched ${records} != ${column.records}")
      column.records
  }
}

object Table {
  def apply(input: Seq[Column[Any]]): Table = {
    val columns = input.map(_.header)
    new Table(columns.zip(input).toMap)
  }
}
