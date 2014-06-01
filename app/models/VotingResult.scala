package models

import play.api.libs.json.Json
import utils.Conversions._

case class VotingResult(names: Seq[String], scores: Seq[Int])

object VotingResult {
  implicit val format = Json.format[VotingResult]
}
