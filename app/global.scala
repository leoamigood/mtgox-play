/**
 * Created with IntelliJ IDEA.
 * User: lamigud
 * Date: 5/27/13
 * Time: 6:49 PM
 * To change this template use File | Settings | File Templates.
 */

import com.amigood.JsonConverts
import io.backchat.hookup._
import io.backchat.hookup.Disconnected
import io.backchat.hookup.HookupClientConfig
import io.backchat.hookup.JsonMessage
import java.net.URI
import java.text.SimpleDateFormat
import net.liftweb.json.JsonAST._
import play.api._

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
          println("The websocket to " + uri + " disconnected.")
        case JsonMessage(message) => {
          (message \ "ticker") match {
            case obj: JObject =>
              collection.insert(JsonConverts.convert(message))
              report(message)
            case _ =>
          }
        }
      }

      def report(message: JValue) = {
        //        println(pretty(JsonAST.render(message)))
        val now = message \ "ticker" \ "now" match {
          case JString(s) => s.toLong / 1000
          case _ => throw new Exception("Unable to parse: " + message)
        }

        println(format.format(now) + " - " + (message \ "ticker" \ "last" \ "display_short").values)
      }

      val f = connect()

      f onSuccess {
        case Success =>
          println("The websocket is connected to:" + uri + ".")
        case _ =>
      }

      f onFailure {
        case _ =>
          println("The websocket failed to connect to:" + uri + ".")
      }

    }
  }

}