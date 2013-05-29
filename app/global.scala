/**
 * Created with IntelliJ IDEA.
 * User: lamigud
 * Date: 5/27/13
 * Time: 6:49 PM
 * To change this template use File | Settings | File Templates.
 */

import io.backchat.hookup._
import io.backchat.hookup.Disconnected
import io.backchat.hookup.HookupClientConfig
import io.backchat.hookup.JsonMessage
import java.net.URI
import java.text.SimpleDateFormat
import net.liftweb.json.{Printer, JsonAST}
import net.liftweb.json.JsonAST._
import play.api._

import play.api.libs.json.Json
import play.api.Play.current

import play.modules.reactivemongo._
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.ExecutionContext.Implicits.global

//use 'play start' to start play in production mode (auto start)
object Global extends GlobalSettings {

  val format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aaa")

  override def onStart(app: Application) {
    val db = ReactiveMongoPlugin.db
    val collection = db.collection[JSONCollection]("mtgox")

    val uri = new URI("ws://websocket.mtgox.com:80/mtgox")

    new DefaultHookupClient(HookupClientConfig(uri)) {
      def receive = {
        case Connected =>
        //          send(TextMessage("{\"channel\":\"d5f06780-30a8-4a48-a2f8-7ed181b4a13f\",  \"op\":\"subscribe\"}"))
        case Disconnected(_) =>
          Logger.info("The websocket to " + uri + " disconnected.")
        case JsonMessage(message) => {
          (message \ "ticker") match {
            case obj: JObject =>
              collection.insert(Json.parse(Printer.compact((JsonAST.render(message)))))
              if (Logger.isDebugEnabled) report(message)
            case _ =>
          }
        }
      }

      def report(message: JValue) = {
        //Logger.trace(pretty(JsonAST.render(message)))
        val now = message \ "ticker" \ "now" match {
          case JString(s) => s.toLong / 1000
          case _ => throw new Exception("Unable to parse: " + message)
        }

        Logger.debug(format.format(now) + " - " + (message \ "ticker" \ "last" \ "display_short").values)
      }

      val f = connect()

      f onSuccess {
        case Success =>
          Logger.info("The websocket is connected to:" + uri + ".")
        case _ =>
      }

      f onFailure {
        case _ =>
          Logger.error("The websocket failed to connect to:" + uri + ".")
      }

    }
  }

}