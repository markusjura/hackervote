package base

import play.api._
import play.api.Play._
import com.mongodb.casbah.{MongoURI, MongoConnection}
import com.mongodb.casbah.commons.conversions.scala._
import com.mongodb.casbah.commons.MongoDBObject

object Global extends GlobalSettings {

  override def onStart(app: Application): Unit = {
    // Connection to DB..
    val uri = MongoURI(configuration.getString("mongodb.default.uri").get)
    val connection = MongoConnection(uri)
    val db = connection(configuration.getString("mongodb.default.db").get)

    // Ensure indexes - collections and columns
    db("organisation").ensureIndex(MongoDBObject("name" -> 1), "name_1", true)
    db("organisation").ensureIndex(MongoDBObject("apiKey" -> 1), "apiKey_1", true)
    db("user").ensureIndex(MongoDBObject("email" -> 1), "email_1", true)
    db("user").ensureIndex("resetToken")
    db("app").ensureIndex("userIds")
    db("app").ensureIndex(MongoDBObject("name" -> 1), "name_1", true)
    db("testrun").ensureIndex("appId")
    db("testrun.testcase").ensureIndex("testrunId")
    db("testcase").ensureIndex("appId")
    db("newsletter").ensureIndex("email")
    db("waitinglist").ensureIndex("email")

    // Register Joda time conversion..
    RegisterJodaTimeConversionHelpers()
    RegisterConversionHelpers()
  }
}
