package controllers

import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def ticker = Action { request =>
    Ok(views.html.ticker("Welcome to Mt.Minsi: Ticker")(request))
  }

  def order = Action { request =>
    Ok(views.html.order("Welcome to Mt.Minsi: Orders")(request))
  }

}