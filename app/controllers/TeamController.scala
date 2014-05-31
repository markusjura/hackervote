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

  def displayForm = Action {
    Ok(views.html.register(registrationForm))
  }

  def submitForm = Action { request =>



    val hackervoteTeam = Team(
      profileInfo = ProfileInfo(
        team = "hackervote",
        password = "password"
      )
    )

    Team.add(hackervoteTeam)

    Ok("Added team - Check console")
  }

  private def onError(badForm: Form[LoginData]) =
      BadRequest(views.html.reqister(badForm))


  private def onSuccess(data: LoginData) = {

    Redirect(routes.Application.index()).withSession("team" -> data.team)
  }

}
