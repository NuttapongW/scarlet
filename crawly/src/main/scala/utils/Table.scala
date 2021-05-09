package org.lyghtning
package utils

class Table(data: Map[String, Column[Any]]) {
  val records = data.foldLeft(-1) {
    case (records, (name, column)) => {
      assert(records < 0 || records == column.records, s"Column[${name}] size mismatched ${records} != ${column.records}")
      column.records
    }
  }
}

object Table {
  def apply(input: Seq[Column[Any]]): Table = {
    val columns = input.zipWithIndex.map { case (col, idx) => col.header.getOrElse(s"column_${idx}")}
    new Table(columns.zip(input).toMap)
  }
}
