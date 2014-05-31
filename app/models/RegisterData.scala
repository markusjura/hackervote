package models

import com.mongodb.casbah.Imports._
import mongoContext._
import se.radley.plugin.salat._
import com.novus.salat.dao._
import play.api.Play.current

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
  override val dao = new SalatDAO[Team, ObjectId](collection = mongoCollection("team")) {}

  def getByTeamName(teamName: String): Either[String, Team] = {
    dao.findOne(MongoDBObject("profileInfo.team" -> teamName)) match {
      case None => Left(s"Team with name $teamName does not exist")
      case Some(team) => Right(team)
    }
  }
}
