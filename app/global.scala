/**
 * Created with IntelliJ IDEA.
 * User: lamigud
 * Date: 5/27/13
 * Time: 6:49 PM
 * To change this template use File | Settings | File Templates.
 */
package global.scala

import io.backchat.hookup._
import io.backchat.hookup.Disconnected
import io.backchat.hookup.HookupClientConfig
import io.backchat.hookup.JsonMessage
import java.net.URI
import play.api._

import play.api.libs.json.Json
import play.api.Play.current

import play.modules.reactivemongo._
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.ExecutionContext.Implicits.global
import com.amigood.minsi.{WebsocketClient}

//use 'play start' to start play in production mode (auto start)
object Global extends GlobalSettings {

  lazy val db = ReactiveMongoPlugin.db.collection[JSONCollection]("mtgox")
  lazy val orders = ReactiveMongoPlugin.db.collection[JSONCollection]("orders")
  lazy val wClient = WebsocketClient.create

  override def onStart(app: Application) {
     wClient
  }

}