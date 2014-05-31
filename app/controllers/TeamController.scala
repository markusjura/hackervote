package controllers

import play.api.mvc._
import models._
import play.api.Logger

object TeamController extends Controller {

  def addTeam = Action { request =>
    val hackervoteTeam = Team(
      profileInfo = ProfileInfo(
        team = "hackervote",
        password = "password"
      )
    )

    Team.add(hackervoteTeam)

    Ok("Added team - Check console")
  }
}
