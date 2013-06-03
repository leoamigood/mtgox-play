package com.amigood.minsi

import global.scala.Global._
import io.backchat.hookup._
import play.api.Logger
import net.liftweb.json.JsonAST.{JString, JObject}
import play.api.libs.json.Json
import net.liftweb.json.{JsonAST, Printer}
import io.backchat.hookup.HookupClientConfig
import io.backchat.hookup.Disconnected
import net.liftweb.json.JsonAST.JObject
import net.liftweb.json.JsonAST.JString
import io.backchat.hookup.JsonMessage
import java.net.URI

/**
 * Created with IntelliJ IDEA.
 * User: lamigud
 * Date: 6/3/13
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */

object WebsocketClient {

  private val uri = new URI("ws://websocket.mtgox.com:80/mtgox")

  def create = {
    new DefaultHookupClient(HookupClientConfig(uri)) {
      def receive = {
        case Connected =>
        //          send(TextMessage("{\"channel\":\"d5f06780-30a8-4a48-a2f8-7ed181b4a13f\",  \"op\":\"subscribe\"}"))
        case Disconnected(_) =>
          Logger.info("The websocket to " + uri + " disconnected.")
          Logger.info("Attemping to reconnect...")
          reconnect()
        case JsonMessage(message) => {
          (message \ "ticker") match {
            case JObject(_) =>
              val channel = message \ "channel" match {
                case JString(s) => s
                case _ => throw new Exception("No channel data: " + message)
              }

              WebsocketServer.publish(channel, message)

              collection.insert(Json.parse(Printer.compact((JsonAST.render(message)))))
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
