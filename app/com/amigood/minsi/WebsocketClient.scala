package com.amigood.minsi

import global.scala.Global._
import io.backchat.hookup._
import play.api.Logger
import play.api.libs.json.Json
import net.liftweb.json._
import java.net.URI
import controllers.{SocketApplication, Application}
import play.api.libs.iteratee.{Enumerator, Concurrent}
import io.backchat.hookup.HookupClientConfig
import io.backchat.hookup.Disconnected
import io.backchat.hookup.JsonMessage
import java.util.concurrent.ConcurrentHashMap

import scala.collection
import collection.JavaConverters._
import java.text.SimpleDateFormat


/**
 * Created with IntelliJ IDEA.
 * User: lamigud
 * Date: 6/3/13
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */

object WebsocketClient {

  private val format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aaa")
  private val uri = new URI("ws://websocket.mtgox.com:80/mtgox")

  val subscriptions = new ConcurrentHashMap[String, Set[Concurrent.Channel[JValue]]]().asScala.withDefaultValue(Set.empty)

  def publish(topic: String, data: JValue) {
    val text = excerpt(data)
    Logger.debug("Publihsed %s => %s".format(topic, text.values(1)))
    subscriptions(topic) foreach {
      _ push text
    }
  }

  def excerpt(data: JValue) = {
    val now: JString  = data \ "ticker" \ "now" match {
      case JString(s) => new JString(format.format(s.toLong / 1000))
      case _ => throw new Exception("Unable to parse: " + data)
    }
    val short = data \ "ticker" \ "last" \ "display_short"

    JArray(now :: short :: Nil)
  }

  def subscribe(topic: String, client: Concurrent.Channel[JValue]) {
    subscriptions(topic) += client
  }

  def unsubscribe(topic: String, client: Concurrent.Channel[JValue]) {
    subscriptions(topic) -= client
  }

  def unsubscribe(client: Concurrent.Channel[JValue]) {
    subscriptions foreach { case (topic, clients) =>
      clients - client
    }
  }

  def create = {
    new DefaultHookupClient(HookupClientConfig(uri)) {
      def receive = {
        case Connected =>
        //          send(TextMessage("{\"channel\":\"d5f06780-30a8-4a48-a2f8-7ed181b4a13f\",  \"op\":\"subscribe\"}"))
        case Disconnected(_) =>
          Logger.info("The websocket to " + uri + " disconnected.")
//          Logger.info("Attemping to reconnect...")
//          reconnect()
        case JsonMessage(message) => {
          (message \ "ticker") match {
            case JObject(_) =>
              val channel = message \ "channel" match {
                case JString(s) => s
                case _ => throw new Exception("No channel data: " + message)
              }

              publish(channel, message)

              db.insert(Json.parse(Printer.compact((JsonAST.render(message)))))
            case _ =>
          }
        }
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
