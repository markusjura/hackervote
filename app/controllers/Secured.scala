package controllers

import play.api.mvc._
import scala.concurrent.Future

trait Secured extends Controller {

  /**
   * Wrapped request which contains the request and the team name
   */
  class TeamRequest[A](val team: String, request: Request[A]) extends WrappedRequest[A](request)

  object Auth extends ActionBuilder[TeamRequest] {
    override def invokeBlock[A](request: Request[A], block: (TeamRequest[A]) => Future[Result]) = {
      authenticate(request) match {
        case None => Future.successful(Redirect(routes.Application.login()))
        case Some(team) => block(new TeamRequest(team, request))
      }
    }
  }

  def authenticate[A](request: Request[A]): Option[String] =
    request.session.get("team")
}
