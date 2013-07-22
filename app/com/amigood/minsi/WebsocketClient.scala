package com.amigood.minsi

import global.scala.Global._
import io.backchat.hookup._
import play.api.Logger
import play.api.libs.json.Json
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
import scala.util.control.Breaks._
import scala.compat.Platform
import com.roundeights.hasher.Implicits._

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.apache.commons.codec.binary.Base64
import java.lang.Integer
import com.roundeights.hasher.Implicits._
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Mac
import play.Play
import com.roundeights.hasher.Hasher

/**
 * Created with IntelliJ IDEA.
 * User: lamigud
 * Date: 6/3/13
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */

object WebsocketClient {

  private val format = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss aaa")
  private val uri = new URI("ws://websocket.mtgox.com:80/mtgox?Currency=USD,EUR")

  var totalClients: Int = 0
  val subscriptions = new ConcurrentHashMap[String, Set[Concurrent.Channel[JValue]]]().asScala.withDefaultValue(Set.empty)

  def publish(topic: String, data: JValue) {

    def extract(data: JValue) = {
      val now: JString  = data \ "ticker" \ "now" match {
        case JString(s) => new JString(format.format(s.toLong / 1000))
        case _ => throw new Exception("Unable to parse: " + data)
      }
      val short = data \ "ticker" \ "last" \ "display_short"

      new JArray(now :: short :: Nil)
    }

    val text = extract(data)
    Logger.debug("Published %s => %s".format(topic, text.values(1)))
    Logger.debug("Total clients: %s, channels: %s".format(totalClients, subscriptions.size))
    subscriptions(topic) foreach {
      _ push text
    }
  }

  def register(channel: String) {
    wClient.send(JsonMessage(parse(s"""{ "channel":"$channel", "op":"subscribe" }""")))
  }

  def authenticate(endpoint: String) {
    def hex2bytes(hex: String): Array[Byte] = {
      hex.filter(_ != '-').grouped(2).toArray.map(Integer.parseInt(_, 16).toByte)
    }

    val apiKey: Array[Byte] = hex2bytes(Play.application.configuration.getString("mtgox.apiKey"))
    val apiSecret: Array[Byte] = Base64.decodeBase64(Play.application.configuration.getString("mtgox.apiSecret") getBytes)

    val nonce = Platform.currentTime.toString
    val requestId = nonce.md5
    val call: String = s"""{ "id":"$requestId", "call":"$endpoint", "nonce":"$nonce", "params":"", "item":"BTC" }"""
    val query: Array[Byte] = apiKey ++ call.hmac(apiSecret).sha512.bytes ++ call.getBytes
    wClient.send(s""" {"op":"call", "id":"$requestId", "call":"${new String(Base64.encodeBase64(query))}", "context":"mtgox.com"} """)
  }


  def subscribe(topic: String, client: Concurrent.Channel[JValue]) {
    breakable {
      for (sub <- subscriptions) {
        if (sub._2.contains(client)) break
      }
      totalClients += 1
    }

    if (subscriptions(topic).isEmpty) {
      Logger.debug("Registering new channel: " + topic)
      register(topic)
    }
    subscriptions(topic) += client
  }

  def unsubscribe(client: Concurrent.Channel[JValue]) {
    subscriptions foreach { case (topic, clients) =>
      subscriptions(topic) -= client
    }
    totalClients -= 1
  }

  def create = {
    new DefaultHookupClient(HookupClientConfig(uri)) {
      def receive = {
        case Connected =>
          Logger.debug("Server connected!")
          //send(TextMessage("{\"channel\":\"d5f06780-30a8-4a48-a2f8-7ed181b4a13f\",  \"op\":\"unsubscribe\"}"))
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

              db.insert(Json.parse(compact((render(message)))))
            case _ =>
          }

          if(message \ "channel" == JNothing) println(pretty(render(message)))
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
