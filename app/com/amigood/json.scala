package com.amigood

import scala._
import net.liftweb.json.JsonAST._
import play.api.libs.json._

/**
 * Created with IntelliJ IDEA.
 * User: lamigud
 * Date: 5/27/13
 * Time: 5:41 PM
 * To change this template use File | Settings | File Templates.
 */
object JsonConverts {
  def convert(json: JValue): JsValue = json match {
    case JNull => JsNull
    case JBool(b) => JsBoolean(b)
    case JString(s) => JsString(s)
    case JInt(i) => JsNumber(BigDecimal(i))
    case JDouble(d) => JsNumber(d)
    case JArray(a) => JsArray(a.map(i => convert(i)))
    case JField(name, value) => convert(value)
    case JObject(obj) => JsObject(obj.map(f => (f.name, convert(f)) ))
    case _ => JsUndefined(json.toString)
  }
}
