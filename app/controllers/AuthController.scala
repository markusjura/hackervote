package controllers

import play.api.mvc._
import models._
import play.api.data.Form
import play.api.data.Forms._
import models.ProfileInfo
import com.mongodb.MongoException.DuplicateKey
import play.Logger

object AuthController extends Secured {

  val registrationForm: Form[ProfileInfo] = Form {
    mapping(
      "team" -> nonEmptyText(minLength = 4), //verify the username
      "password" -> nonEmptyText(minLength = 6), //verify the password
      "githubUrl" -> optional(text),
      "appUrl" -> optional(text),
      "members" -> list(mapping(
        "name" -> nonEmptyText(minLength = 4),
        "email" -> email
      )(Member.apply)(Member.unapply))
    )(ProfileInfo.apply)(ProfileInfo.unapply)
  }

  def register = Action { implicit request =>
    Ok(views.html.register(registrationForm))
  }

  def registerSubmit = Action { implicit request =>
    def onError(badForm: Form[ProfileInfo])(implicit request: RequestHeader) =
    BadRequest(views.html.register(badForm))


    def onSuccess(profileInfo: ProfileInfo)(implicit request: RequestHeader) = {
      try {
        Team.add(Team(profileInfo = profileInfo))
        Redirect(routes.Application.index()).withSession("team" -> profileInfo.team)
      } catch {
        case dk: DuplicateKey => {
          Logger.info(s"duplicate key $dk")
          BadRequest(views.html.register(registrationForm.fill(profileInfo).withGlobalError("Team already exists")))
        }
        case e: Exception => {
          Logger.info(s"exception $e")
          BadRequest(views.html.register(registrationForm.fill(profileInfo).withGlobalError("Can't create a team")))
        }
      }
    }

    registrationForm.bindFromRequest.fold(onError, onSuccess)
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
    def onError(badForm: Form[LoginData])(implicit request: RequestHeader) =
      BadRequest(views.html.login(badForm))

    def onSuccess(loginData: LoginData)(implicit request: RequestHeader) = {
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

    loginForm.bindFromRequest.fold(onError, onSuccess)
  }

  def logout = Action { implicit request =>
    Redirect(routes.AuthController.login()).withNewSession
  }

}
