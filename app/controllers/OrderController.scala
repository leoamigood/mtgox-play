package controllers

import global.scala.Global._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models._
import play.api.libs.json._
import com.amigood.minsi.OrderDAO

/**
 * Created with IntelliJ IDEA.
 * User: leo
 * Date: 7/27/13
 * Time: 2:07 PM
 * To change this template use File | Settings | File Templates.
 */
object OrderController extends Controller {
  val dao = OrderDAO

  val buyForm: Form[models.Bid] = Form(
    mapping (
      "amount" -> number,
      "price" -> number
    )(Bid.apply)(Bid.unapply)
  )

  val sellForm: Form[models.Ask] = Form(
    mapping (
      "amount" -> number,
      "price" -> number
    )(Ask.apply)(Ask.unapply)
  )

  def buy = Action { implicit request =>
    val form: Form[Bid] = buyForm.bindFromRequest
    form.fold(
      errors => BadRequest(views.html.buy(errors)),

      order => {
        dao.save(order)
        Ok(views.html.buy(form))
      }
    )
  }

  def sell = Action { implicit request =>
    val form: Form[Ask] = sellForm.bindFromRequest
    form.fold(
      errors => BadRequest(views.html.sell(errors)),
      order => Ok(views.html.sell(form))
    )
  }

}
