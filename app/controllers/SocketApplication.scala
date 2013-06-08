package controllers

import java.text.SimpleDateFormat
import java.util.concurrent.ConcurrentHashMap
import play.api.libs.iteratee.{Concurrent, Input, Iteratee, Enumerator}
import net.liftweb.json.JsonAST._
import play.api.Logger
import play.api.mvc.WebSocket
import play.libs.Json

import collection.JavaConverters._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import java.text.SimpleDateFormat
import net.liftweb.json._
import com.amigood.minsi.WebsocketClient._
import net.liftweb.json.JsonAST.JValue
import net.liftweb.json.JsonAST.JString
import net.liftweb.json.JsonAST.JArray
import play.api.mvc.WebSocket.FrameFormatter
import play.core.server.websocket
import play.api.libs.json.JsString

/**
 * Created with IntelliJ IDEA.
 * User: lamigud
 * Date: 6/4/13
 * Time: 12:15 AM
 * To change this template use File | Settings | File Templates.
 */
object SocketApplication {

  implicit val jsonLiftFrame: WebSocket.FrameFormatter[JValue] = WebSocket.FrameFormatter.stringFrame.transform(
    json => Printer.pretty((JsonAST.render(json))),
    s => JsonParser.parse(s)
  )

  def stream = WebSocket.using[JValue] { request =>

    val (out, channel) = Concurrent.broadcast[JValue]

    val in = Iteratee.foreach[JValue]({ json =>
      subscribe(json \ "subscribe" match {
        case JString(s) => s
        case _ => ""
      }, channel)
    }).mapDone { _ =>
      unsubscribe(channel)
    }

    (in, out)
  }
}
