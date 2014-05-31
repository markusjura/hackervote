package models

import org.bson.types.ObjectId
import com.mongodb.casbah.Imports._
import mongoContext._
import com.novus.salat.annotations._
import com.mongodb.casbah.Imports
import se.radley.plugin.salat._
import com.novus.salat.dao._
import play.api.Play.current
import play.api.libs.json.Json
import utils.json.JsonReadsWrites._

case class Team(id: ObjectId = new ObjectId,
                profileInfo: ProfileInfo,
                vote: Option[Vote] = None,
                score: Int = 0) extends MongoObject

case class Vote(firstPlace: String, secondPlace: String, thirdPlace: String)

case class ProfileInfo(team: String,
                       password: String,
                       githubUrl: Option[String] = None,
                       appUrl: Option[String] = None,
                       members: Option[List[Member]] = None)

case class Member(name: String, email: String)

object Team extends MongoCompanion[Team, ObjectId] {

  implicit val memberFormat = Json.format[Member]
  implicit val profileInfoFormat = Json.format[ProfileInfo]
  implicit val voteFormat = Json.format[Vote]
  implicit val teamFormat = Json.format[Team]

  override val dao = new SalatDAO[Team, ObjectId](collection = mongoCollection("team")) {}
}
