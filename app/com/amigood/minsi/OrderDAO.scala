package com.amigood.minsi

import global.scala.Global._
import models._
import models.Bid
import models.Ask
import play.api.libs.json.Json
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger


/**
 * Created with IntelliJ IDEA.
 * User: leo
 * Date: 7/27/13
 * Time: 5:00 PM
 * To change this template use File | Settings | File Templates.
 */
object OrderDAO {

  def save(order: Order) {
    order match {
      case Bid(amount, price) => persist("Buy", amount, price)
      case Ask(amount, price) => persist("Sell", amount, price)
      case _ => throw new NotImplementedError
    }

    def persist(name: String, amount: Int, price: Int) = {
      val json = Json.toJson(
        Map(
          "order" -> Json.toJson(name),
          "amount" -> Json.toJson(amount),
          "price" -> Json.toJson(price)
        )
      )
      Logger.debug("Order %s".format(json))
      orders.insert(json)
    }
  }

}
