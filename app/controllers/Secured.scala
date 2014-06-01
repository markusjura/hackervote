package controllers

import play.api.mvc._
import scala.concurrent.Future
import models.Team

trait Secured extends Controller {

  /**
   * Wrapped request which contains the request and the team name
   */
  class TeamNameRequest[A](val teamName: String, request: Request[A]) extends WrappedRequest[A](request)

  object Auth extends ActionBuilder[TeamNameRequest] {
    override def invokeBlock[A](request: Request[A], block: (TeamNameRequest[A]) => Future[Result]) = {
      authenticate(request) match {
        case None => Future.successful(Redirect(routes.AuthController.login()))
        case Some(team) => block(new TeamNameRequest(team, request))
      }
    }
  }

  private def authenticate[A](request: Request[A]): Option[String] =
    request.session.get("team")


  class TeamRequest[A](val team: Team, request: Request[A]) extends WrappedRequest[A](request)

  object WithTeam extends ActionRefiner[TeamNameRequest, TeamRequest] {
    def refine[A](request: TeamNameRequest[A]) = Future.successful {
      Team.getByTeamName(request.teamName).fold(
        error => Left(Redirect(routes.AuthController.logout())),
        team => Right(new TeamRequest(team, request))
      )
    }
  }

  val AuthWithTeam = Auth andThen WithTeam
}
