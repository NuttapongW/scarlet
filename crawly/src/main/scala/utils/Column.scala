package utils

case class Column[T](header: String, values: Seq[T]) {
  val records = values.size
}

object ColumnUtils {

  sealed trait DType
  final case object IntType extends DType
  final case object LongType extends DType
  final case object FloatType extends DType
  final case object StringType extends DType

  private[utils] def getTypedColumn(name: String, values: Seq[String], dType: DType): Column[Any] = {
     dType match {
       case IntType => Column(name, values.map(cleanNumericString(_).toInt))
       case LongType => Column(name, values.map(cleanNumericString(_).toLong))
       case FloatType => Column(name, values.map(cleanNumericString(_).toFloat))
       case StringType => Column(name, values)
       case t: DType => throw new Exception(s"Unsupported type conversion ($t)")
     }
  }

  private[utils] def cleanNumericString(num: String): String = num.trim.filterNot(_ == ',')

  def getColumns(
    columnNames: Seq[String],
    rows: Seq[Seq[String]],
    dTypes: Map[String, DType] = Map.empty,
    defaultType: DType = FloatType
  ): Seq[Column[Any]] = {

    val transposed = rows.transpose
    assert(columnNames.size == transposed.size)
    columnNames.zip(transposed).map {
          case (name, seq) => getTypedColumn(name, seq, dTypes.getOrElse(name, defaultType))
    }
  }
}
