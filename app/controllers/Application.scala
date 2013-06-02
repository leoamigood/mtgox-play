package controllers

import play.api._
import play.api.mvc._
import concurrent._
import akka.dispatch._
import java.util.concurrent.Executors
import play.api.libs.iteratee.{Enumerator, Iteratee}

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def ticker = Action { implicit request =>
    Ok(views.html.ticker("Your new application is ready. Ticker"))
  }

  def socket = WebSocket.using[String] { request =>

  // Log events to the console
    val in = Iteratee.foreach[String](println).mapDone { _ =>
      println("Disconnected")
    }

    // Send a single 'Hello!' message
    val out = Enumerator("Hello!")

    (in, out)
  }

}