import org.specs2.mutable.Specification

import org.json4s._
import org.json4s.jackson.JsonMethods._

/**
 * Created with IntelliJ IDEA.
 * User: lamigud
 * Date: 6/16/13
 * Time: 10:44 PM
 * To change this template use File | Settings | File Templates.
 */
class ClientTest extends Specification {

  "json4s" should {
    """parse and marshall '{ "channel":"d5f06780-30a8-4a48-a2f8-7ed181b4a13f", "op":"subscribe" }'""" in {
      compact (render (parse ("""{ "channel":"d5f06780-30a8-4a48-a2f8-7ed181b4a13f", "op":"subscribe" }"""))) mustEqual
        """{"channel":"d5f06780-30a8-4a48-a2f8-7ed181b4a13f","op":"subscribe"}"""
    }
  }
}
