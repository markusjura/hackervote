package controllers

import play.api.mvc._
import models._
import play.api.data.Form
import play.api.data.Forms._
import models.LoginData
import models.ProfileInfo

object TeamController extends Controller {

  val registrationForm: Form[LoginData] = Form {
    mapping(
      "team" -> nonEmptyText(minLength = 4), //verify the username
      "password" -> nonEmptyText(minLength = 6) //verify the password
    )(LoginData.apply)(LoginData.unapply)
  }

  def displayForm = Action { implicit request =>
    Ok(views.html.register(registrationForm))
  }

  def submitForm = Action { implicit request =>

    registrationForm.bindFromRequest.fold(onError, onSuccess)
  }

  private def onError(badForm: Form[LoginData])(implicit request: RequestHeader) =
      BadRequest(views.html.register(badForm))


  private def onSuccess(data: LoginData)(implicit request: RequestHeader) = {

    val newTeam = Team(
      profileInfo = ProfileInfo(
        team = data.team,
        password = data.password
      )
    )

    Team.add(newTeam)

    Redirect(routes.Application.index()).withSession("team" -> data.team)
  }

}
