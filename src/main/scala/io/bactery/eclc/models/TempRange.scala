package io.bactery.eclc.models

import enumeratum._

sealed trait TempRange extends EnumEntry

object TempRange extends Enum[TempRange] {

  val values = findValues

  case object WtfCold extends TempRange
  case object Coldest extends TempRange
  case object VeryCold extends TempRange
  case object PrettyCold extends TempRange
  case object Cold extends TempRange
  case object Neutral extends TempRange
  case object Ideal extends TempRange
  case object Hot extends TempRange
  case object PrettyHot extends TempRange
  case object VeryHot extends TempRange
  case object Hottest extends TempRange
  case object WtfHot extends TempRange
  case object Uncategorized extends TempRange

  def getRange(temp: BigDecimal): TempRange = temp match {
    case x if Range(-100, -20).contains(x) => WtfCold
    case x if Range(-19, -10).contains(x) => Coldest
    case x if Range(-9, -1).contains(x) => VeryCold
    case x if Range(0, 6).contains(x) => PrettyCold
    case x if Range(7, 12).contains(x) => Cold
    case x if Range(13, 18).contains(x) => Neutral
    case x if Range(19, 24).contains(x) => Ideal
    case x if Range(25, 29).contains(x) => Hot
    case x if Range(30, 35).contains(x) => PrettyHot
    case x if Range(36, 40).contains(x) => VeryHot
    case x if Range(41, 45).contains(x) => Hottest
    case x if Range(46, 55).contains(x) => WtfHot
    case _ => Uncategorized
  }

  def getTrueTemp(hour: Int, temp: BigDecimal): TempRange = {
    val tempDiff = BigDecimal(Math.abs(hour-12)) match {
      case x if x <= 2 => x * 2
      case x if x <= 4 => 1.5 * x
      case x => 1 * x
    }

    getRange(temp + tempDiff)
  }
}