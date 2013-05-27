package controllers

import play.api.mvc.{Action, Controller}
import java.net.URI
import io.backchat.hookup._
import io.backchat.hookup.HookupClientConfig
import io.backchat.hookup.Disconnected
import io.backchat.hookup.JsonMessage
import java.text.SimpleDateFormat
import net.liftweb.json.JsonAST._
import play.api.Play.current

import play.modules.reactivemongo._
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.ExecutionContext.Implicits.global

import com.amigood._

/**
 * Created with IntelliJ IDEA.
 * User: lamigud
 * Date: 5/18/13
 * Time: 2:40 AM
 * To change this template use File | Settings | File Templates.
 */
object Websocket extends Controller with MongoController {

  val format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aaa")

  def web = Action {

    val uri = new URI("ws://websocket.mtgox.com:80/mtgox")

    val db = ReactiveMongoPlugin.db
    val collection = db.collection[JSONCollection]("mtgox")

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

//              println(pretty(JsonAST.render(message)))
              println(format.format((obj \ "now") match { case JString(s) => s.toLong / 1000 }) + " - " + (obj \ "last" \ "display_short").values )
            case _ =>
          }
        }
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

    Ok(views.html.ticker("Your new application is ready."))
  }

}
