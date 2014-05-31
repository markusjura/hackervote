package controllers

import play.api._
import play.api.mvc._
import models._
import play.api.data.Form
import play.api.data.Forms._

object Application extends Controller {

  def index = Action { request =>
    Ok("This is our index page")
  }

  def login = Action { request =>
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

  private def onError(badForm: Form[LoginData]) =
    BadRequest(views.html.login(badForm))

  private def onSuccess(loginData: LoginData) = {


    Redirect(routes.Application.index()).withSession("team" -> loginData.team)

  }


  def register = Action { request =>
    Ok("Register")
  }

}