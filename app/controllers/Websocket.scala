package controllers

import play.api.mvc.{Action, Controller}
import java.net.URI
import io.backchat.hookup._
import io.backchat.hookup.HookupClientConfig
import io.backchat.hookup.Disconnected
import net.liftweb.json.JsonAST.JString
import io.backchat.hookup.JsonMessage
import java.text.SimpleDateFormat
import net.liftweb.json.Printer._
import net.liftweb.json.JsonAST._

/**
 * Created with IntelliJ IDEA.
 * User: lamigud
 * Date: 5/18/13
 * Time: 2:40 AM
 * To change this template use File | Settings | File Templates.
 */
object Websocket extends Controller {

  val format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aaa")

  def web = Action {

    val uri = new URI("ws://websocket.mtgox.com:80/mtgox")

    val client = new DefaultHookupClient(HookupClientConfig(uri)) {

      def receive = {
        case Connected =>
//          send(TextMessage("{\"channel\":\"d5f06780-30a8-4a48-a2f8-7ed181b4a13f\",  \"op\":\"subscribe\"}"))
        case Disconnected(_) =>
          println("The websocket to " + uri + " disconnected.")
        case JsonMessage(message) => {
          (message \ "ticker" \ "last" \ "value") match {
            case JString(value) =>
//              println(pretty(render(message)))
              println(format.format(System.currentTimeMillis()) + " - " + value)
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
