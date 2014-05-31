package base

import play.api.GlobalSettings
import com.mongodb.casbah.{MongoURI, MongoConnection}
import com.mongodb.casbah.commons.conversions.scala._
import com.mongodb.casbah.commons.MongoDBObject
import play.api._
import play.api.Play.current

object Global extends GlobalSettings {

  override def onStart(app: Application): Unit = {
    // Connection to DB..
    val conf = play.api.Play.configuration
    val uri = MongoURI(conf.getString("mongodb.default.uri").get)
    val connection = MongoConnection(uri)
    val db = connection(conf.getString("mongodb.default.db").get)

    // Ensure indexes - collections and columns
    db("team").ensureIndex(MongoDBObject("profileInfo.team" -> 1), "teamname_1", true)
    db("team").ensureIndex("score")

    // Register Joda time conversion..
    RegisterJodaTimeConversionHelpers()
    RegisterConversionHelpers()
  }

}
