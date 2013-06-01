package controllers

import play.api._
import play.api.mvc._
import concurrent._
import akka.dispatch._
import java.util.concurrent.Executors

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def ticker = Action {
    Ok(views.html.ticker("Your new application is ready, ticker"))
  }

}