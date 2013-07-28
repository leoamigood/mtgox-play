package models

/**
 * Created with IntelliJ IDEA.
 * User: leo
 * Date: 7/22/13
 * Time: 7:56 PM
 * To change this template use File | Settings | File Templates.
 */
sealed abstract class Order
case class BuyMarket(amount: Int) extends Order
case class SellMarket(amount: Int) extends Order
case class Bid(amount: Int, price: Int) extends Order
case class Ask(amount: Int, price: Int) extends Order
case class StopLoss(amount: Int, price: Int) extends Order
case class BuyLimit(amount: Int, price: Int) extends Order
case class SellLimit(amount: Int, price: Int) extends Order