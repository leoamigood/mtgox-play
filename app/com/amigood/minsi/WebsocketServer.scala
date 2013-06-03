package com.amigood.minsi

import java.text.SimpleDateFormat
import java.util.concurrent.ConcurrentHashMap
import io.backchat.hookup._
import net.liftweb.json.JsonAST._
import play.api.Logger
import io.backchat.hookup.Disconnected
import io.backchat.hookup.JsonMessage
import io.backchat.hookup.TextMessage
import io.backchat.hookup.Disconnected
import io.backchat.hookup.JsonMessage
import io.backchat.hookup.TextMessage
import net.liftweb.json.JsonAST.JField
import io.backchat.hookup.Disconnected
import net.liftweb.json.JsonAST.JObject
import net.liftweb.json.JsonAST.JString
import io.backchat.hookup.JsonMessage
import io.backchat.hookup.TextMessage
import net.liftweb.json.JsonAST.JArray

import collection.JavaConverters._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created with IntelliJ IDEA.
 * User: lamigud
 * Date: 6/3/13
 * Time: 1:21 PM
 * To change this template use File | Settings | File Templates.
 */
object WebsocketServer {

  val format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aaa")

  private val subscriptions = new ConcurrentHashMap[String, Set[HookupServerClient]]().asScala.withDefaultValue(Set.empty)

  def publish(topic: String, data: JValue) {
    val text = excerpt(data)
    Logger.debug("Publihsed %s => %s".format(topic, text.values(1)))
    subscriptions(topic) foreach {
      _ ! text
    }
  }

  def excerpt(data: JValue) = {
    val now: JString  = data \ "ticker" \ "now" match {
      case JString(s) => JString(format.format(s.toLong / 1000))
      case _ => throw new Exception("Unable to parse: " + data)
    }
    val short = data \ "ticker" \ "last" \ "display_short"

    JArray(now :: short :: Nil)
  }

  def subscribe(topic: String, client: HookupServerClient) {
    subscriptions(topic) += client
  }

  def unsubscribe(topic: String, client: HookupServerClient) {
    subscriptions(topic) -= client
  }

  private val server = HookupServer(ServerInfo("Websocket ticker", port = 8128)) {
    new HookupServerClient {
      def receive = {
        case Disconnected(_) ⇒
          subscriptions.keysIterator foreach { subscriptions(_) -= this }
        case Connected ⇒
          Logger.debug("client connected")
        case TextMessage(_) ⇒
          this send "only json messages are allowed"

        case JsonMessage(JObject(JField(c, JString(topic)) :: Nil)) if c.equalsIgnoreCase("subscribe") =>
          subscribe(topic, this)
        case JsonMessage(JObject(JField(c, JString(topic)) :: Nil)) if c.equalsIgnoreCase("unsubscribe") =>
          unsubscribe(topic, this)
      }
    }
  }

  def url = {
    "ws://localhost:8128/"
  }

  def start = {
    Future{ server.start }
  }

}
