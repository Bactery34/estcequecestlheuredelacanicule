package io.bactery.eclc.helpers

import io.bactery.eclc.models.TempRange
import io.bactery.eclc.models.TempRange._

trait PunchLineHelpers {

  val punchlineMaps: Map[TempRange, String] = Map(
    WtfCold -> "",
    Coldest -> "",
    VeryCold -> "",
    PrettyCold -> "",
    Cold -> "",
    Neutral -> "",
    Ideal -> "",
    Hot -> "",
    PrettyHot -> "",
    VeryHot -> "",
    Hottest -> "",
    WtfHot -> ""
  )
}
