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

  def npe = Action {

    implicit val ec = ExecutionContext.fromExecutorService(Executors.newCachedThreadPool())

    val f = Future {
      2 / 0
    }

    f onFailure {
      case npe: NullPointerException =>
        println("Amaized!")
      case ex: Exception =>
        println("Amaized!")
    }

    Ok(views.html.index("Your new application is ready, NPE."))
  }
  
}