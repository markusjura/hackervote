package controllers

import play.api._
import play.api.mvc._
import models._
import play.api.data.Form
import play.api.data.Forms._

object Application extends Secured {
  def index = Auth { implicit teamRequest =>
    Team.getByTeamName(teamRequest.session.get("team").get).fold(
      error => Redirect(routes.Application.logout()),
      team => {
        if(team.profileInfo.isFilled) Ok(views.html.index())
        else Redirect(routes.Application.profileInfo())
      }
    )
  }

  def profileInfo = Auth { team =>
    Ok("profileInfo")
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