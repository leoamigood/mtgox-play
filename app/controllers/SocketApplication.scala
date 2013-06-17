package controllers

import play.api.libs.iteratee.{Concurrent, Iteratee}
import play.api.mvc.WebSocket

import scala.concurrent.ExecutionContext.Implicits.global
import com.amigood.minsi.WebsocketClient._
import org.json4s._
import org.json4s.jackson.JsonMethods._

/**
 * Created with IntelliJ IDEA.
 * User: lamigud
 * Date: 6/4/13
 * Time: 12:15 AM
 * To change this template use File | Settings | File Templates.
 */
object SocketApplication {

  implicit val jsonLiftFrame: WebSocket.FrameFormatter[JValue] = WebSocket.FrameFormatter.stringFrame.transform(
    json => pretty((render(json))),
    s => parse(s)
  )

  def stream = WebSocket.using[JValue] { request =>

    val (out, channel) = Concurrent.broadcast[JValue]

    val in = Iteratee.foreach[JValue]({ json =>
      json \ "subscribe" match {
        case JString(s) => subscribe(s, channel)
        case _ =>
      }
    }).mapDone { _ =>
      unsubscribe(channel)
    }

    (in, out)
  }
}
