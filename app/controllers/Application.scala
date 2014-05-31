package controllers

import play.api.mvc._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.Logger

object Application extends Secured {
  def index = AuthWithTeam { implicit teamRequest =>
    Logger.debug(s"filled: ${teamRequest.team.profileInfo.isFilled}")

    if(teamRequest.team.profileInfo.isFilled)
      Ok(views.html.index())
    else
      Redirect(routes.Application.profileInfo())
  }

  def profileInfoForm(profileInfo: ProfileInfo): Form[ProfileInfo] = Form {
    mapping(
      "team" -> ignored(profileInfo.team),
      "password" -> ignored(profileInfo.password),
      "githubUrl" -> optional(nonEmptyText(minLength = 15)),
      "appUrl" -> optional(nonEmptyText(minLength = 15)),
      "members" -> ignored(profileInfo.members)
    )(ProfileInfo.apply)(ProfileInfo.unapply)
  }

  def profileInfo = AuthWithTeam { implicit teamRequest =>
    Ok(views.html.profileInfo(profileInfoForm(teamRequest.team.profileInfo)))
  }

  def profileInfoSubmit = AuthWithTeam { implicit teamRequest =>
    val team: Team = teamRequest.team
    profileInfoForm(team.profileInfo).bindFromRequest.fold(
      badForm => BadRequest(views.html.profileInfo(badForm)),
      pf => {
        Logger.debug(s"pf: $pf")
        Team.update(team.copy(profileInfo = pf))

        Redirect(routes.Application.index())
      }
    )
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

}