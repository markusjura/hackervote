package controllers

import play.api.mvc._
import models._
import play.api.data.Form
import play.api.data.Forms._
import models.ProfileInfo
import com.mongodb.MongoException.DuplicateKey
import play.Logger

object TeamController extends Controller {

  val registrationForm: Form[ProfileInfo] = Form {
    mapping(
      "team" -> nonEmptyText(minLength = 4), //verify the username
      "password" -> nonEmptyText(minLength = 6), //verify the password
      "githubUrl" -> optional(nonEmptyText(minLength = 15)),
      "appUrl" -> optional(nonEmptyText(minLength = 15)),
      "members" -> optional(list(mapping(
        "name" -> text,
        "email" -> text
      )(Member.apply)(Member.unapply)))
    )(ProfileInfo.apply)(ProfileInfo.unapply)
  }

  def displayForm = Action { implicit request =>
    Ok(views.html.register(registrationForm))
  }

  def submitForm = Action { implicit request =>

    registrationForm.bindFromRequest.fold(onError, onSuccess)
  }

  private def onError(badForm: Form[ProfileInfo])(implicit request: RequestHeader) =
      BadRequest(views.html.register(badForm))


  private def onSuccess(profileInfo: ProfileInfo)(implicit request: RequestHeader) = {

    val newTeam = Team(profileInfo = profileInfo)

    try {
      Team.add(newTeam)
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

}
