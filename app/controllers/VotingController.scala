package controllers

import models.{Vote, Team}
import play.api.data.Form
import play.api.data.Forms._
import models.Vote
import scala.Some
import org.bson.types.ObjectId

object VotingController extends Secured {
  def voting = AuthWithTeam { implicit teamRequest =>
    val otherTeams = Team.all.filterNot(_.id == teamRequest.team.id)
    Ok(views.html.voting(otherTeams, votingForm))
  }

  val votingForm: Form[Vote] = Form {
    mapping(
      "firstPlace" -> nonEmptyText(minLength = 12),
      "secondPlace" -> nonEmptyText(minLength = 12),
      "thirdPlace" -> nonEmptyText(minLength = 12)
    )(Vote.apply)(Vote.unapply) verifying(
      "Select for each place a different team.",
      vote => vote.firstPlace != vote.secondPlace && vote.firstPlace != vote.thirdPlace &&
        vote.secondPlace != vote.thirdPlace
      )
  }

  def votingSubmit = AuthWithTeam { implicit teamRequest =>
    votingForm.bindFromRequest.fold(
      badForm => {
        val otherTeams = Team.all.filterNot(_.id == teamRequest.team.id)
        BadRequest(views.html.voting(otherTeams, badForm))
      },
      vote => {
        teamRequest.team.vote match {
          case Some(vote) => {
            val otherTeams = Team.all.filterNot(_.id == teamRequest.team.id)
            BadRequest(views.html.voting(otherTeams, votingForm.withGlobalError("You can't vote twice.")))
          }
          case None => {
            // Update voting for team
            val teamWithVoting = teamRequest.team.copy(vote = Some(vote))
            Team.update(teamWithVoting)

            // Update scores
            val teams = Team.get(Seq(new ObjectId(vote.firstPlace), new ObjectId(vote.secondPlace), new ObjectId(vote.thirdPlace)))
            val firstTeam = teams.filter(vote.firstPlace == _.id.toString).head
            Team.update(firstTeam.copy(score = firstTeam.score + 3))
            val secondTeam = teams.filter(vote.secondPlace == _.id.toString).head
            Team.update(secondTeam.copy(score = secondTeam.score + 2))
            val thirdTeam = teams.filter(vote.thirdPlace == _.id.toString).head
            Team.update(thirdTeam.copy(score = thirdTeam.score + 1))

            Redirect(routes.Application.index())
          }
        }
      }
    )
  }
}
