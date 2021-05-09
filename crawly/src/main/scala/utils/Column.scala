package org.lyghtning
package utils

case class Column[T](header: Option[String] = None, values: Seq[T]) {
  val records = values.size
}

object ColumnUtils {

  sealed trait DType
  final case object IntType extends DType
  final case object LongType extends DType
  final case object FloatType extends DType
  final case object StringType extends DType

  def getTypedColumn(name: Option[String], values: Seq[String], dType: DType): Column[Any] = {
     dType match {
       case IntType => Column(name, values.asInstanceOf[Seq[Int]])
       case LongType => Column(name, values.asInstanceOf[Seq[Long]])
       case FloatType => Column(name, values.asInstanceOf[Seq[Float]])
       case _ => Column(name, values)
     }
  }

  def getColumns(
    columnNames: Option[Seq[String]],
    rows: Seq[Seq[String]],
    dTypes: Map[String, DType],
    defaultType: DType = FloatType
  ): Seq[Column[Any]] = {

    val transposed = rows.transpose
    columnNames match {
      case Some(names) => {
        assert(names.size == transposed.size)
        names.zip(transposed).map {
          case (name, seq) => getTypedColumn(Some(name), seq, dTypes.getOrElse(name, defaultType))
        }
      }
      case _ => transposed.map(getTypedColumn(None, _, defaultType))
    }
  }
}
