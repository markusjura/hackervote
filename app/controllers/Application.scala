package controllers

import play.api._
import play.api.mvc._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Security._

object Application extends Controller {

  /**
   * Retrieve team from session
   */
  def requestTeam(request: RequestHeader) = request.session.get("team")

  /**
   * Redirect to login page
   */
  def onUnauthorized(request: RequestHeader) = Redirect(routes.Application.login)

  def index = Authenticated(requestTeam, onUnauthorized) { team =>
    Action { implicit request =>
      Ok(views.html.index())
    }
  }

  def login = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  val loginForm: Form[LoginData] = Form {
    mapping(
      "team" -> nonEmptyText(minLength = 4), //verify the username
      "password" -> nonEmptyText(minLength = 6) //verify the password
    )(LoginData.apply)(LoginData.unapply)
  }

  def loginSubmit = Action { implicit request =>
    loginForm.bindFromRequest.fold(onError, onSuccess)
  }

  private def onError(badForm: Form[LoginData])(implicit request: RequestHeader) =
    BadRequest(views.html.login(badForm))

  private def onSuccess(loginData: LoginData)(implicit request: RequestHeader) = {
    Team.getByTeamName(loginData.team).fold(
      error => BadRequest(views.html.login(loginForm.fill(loginData).withGlobalError(error))),
      team => {
        if(team.profileInfo.password == loginData.password)
          Redirect(routes.Application.index()).withSession("team" -> loginData.team)
        else
          BadRequest(views.html.login(loginForm.fill(loginData).withGlobalError("Password is incorrect.")))
      }
    )
  }

  def logout = Action { implicit request =>
    Redirect(routes.Application.login()).withNewSession
  }


  def register = Action { request =>
    Ok("Register")
  }

}