package controllers

import play.api.mvc._
import models._
import play.api.libs.json.Json

object Application extends Secured {

  def index = AuthWithTeam { implicit teamRequest =>
    teamRequest.team.vote match {
      case None => Redirect(routes.VotingController.voting())
      case Some(vote) =>
        // Take first 3 teams. Take more teams as long as the score equals the score of the previous team
        val teams = Team.all.sortWith(_.score > _.score).foldLeft(List.empty[Team]) { (teams, currentTeam) =>
          teams match {
            case add if(teams.isEmpty || teams.size < 3 || teams.last.score == currentTeam.score) =>
              teams :+ currentTeam
            case _ => teams
          }
        }

        val names = teams.map(_.profileInfo.team)
        val scores = teams.map(_.score)
        val result = VotingResult(names, scores)

        Ok(views.html.index(Json.stringify(Json.toJson(result))))
    }
  }
}