package utils

import org.scalatest._
import flatspec._
import matchers._
import utils.ColumnUtils.{FloatType, IntType, LongType, StringType}


class ColumnSpec extends AnyFlatSpec with should.Matchers {

  val dataFloat = Seq(1.5f, 2.5f, 3.5f, 4.5f)
  val dataInt = Seq(5, 6, 7, 8)
  val dataLong = Seq(910111213L, 141516171819L, 202122232425L, 0L)
  val dataString = Seq("a", "b", "c", "d")

  val dirtyDataFloat = Seq(" 112,112.112 ", "35,813.21 ")
  val cleanDataFloat = Seq(112112.112f, 35813.21f)

  val dirtyDataInt = Seq(" 112,112 ", "35,813 ")
  val cleanDataInt = Seq(112112, 35813)

  val dirtyDataLong = Seq("910,111,213", "141,516,171,819")
  val cleanDataLong = Seq(910111213L, 141516171819L)

  val numericColumnNames = Seq("col_a", "col_b", "col_c")
  val inCompleteColumnNames = Seq("col_d")
  val columnNames: Seq[String] = numericColumnNames ++ inCompleteColumnNames

  val rawColumns = Seq(
    dataFloat,
    dataInt,
    dataLong,
    dataString
  )

  val rows: Seq[Seq[String]] = rawColumns.map(_.map(_.toString)).transpose

  val dirtyRows: Seq[Seq[String]] = Seq(
    dirtyDataFloat,
    dirtyDataInt,
    dirtyDataLong
  ).transpose

  val cleanColumns = Seq(
    cleanDataFloat,
    cleanDataInt,
    cleanDataLong
  )


  "A column" should "be able to create from raw data" in {

    val columns = ColumnUtils.getColumns(
      columnNames,
      rows,
      columnNames.zip(Seq(FloatType, IntType, LongType, StringType)).toMap
    )

    assert(columns.map(_.header) == columnNames)
    assert(columns.map(_.values) == rawColumns)

  }

  "A column" should "fallback to default data type if no data type provided" in {
    val columns = ColumnUtils.getColumns(
      columnNames,
      rows,
      inCompleteColumnNames.zip(Seq(StringType)).toMap,
      FloatType
    )

    columns foreach {
      case Column(header, values) if !inCompleteColumnNames.contains(header) => assert(values.head.isInstanceOf[Float])
      case Column(_, values) => assert(values.head.isInstanceOf[String])
    }
  }

  "Column creation" should "be able to handle dirty numeric data" in {
    val columns = ColumnUtils.getColumns(
      numericColumnNames,
      dirtyRows,
      numericColumnNames.zip(Seq(FloatType, IntType, LongType)).toMap
    )

    assert(columns.map(_.values) == cleanColumns)
  }

  "Column creation" should "raise error if the numbers of columns and column's names are not matched" in {
    val thrown = intercept[Error] {
      ColumnUtils.getColumns(
        inCompleteColumnNames,
        rows,
        inCompleteColumnNames.zip(Seq(StringType)).toMap,
        FloatType
      )
    }

    assert(thrown.isInstanceOf[AssertionError])
  }

  "Column creation" should "raise error if the data type is incompatible" in {
    val thrown = intercept[Exception] {
      ColumnUtils.getColumns(
        columnNames,
        rows
      )
    }

    assert(thrown.isInstanceOf[NumberFormatException])
  }
}
