import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import com.roundeights.hasher.Implicits._

/**
 * Created with IntelliJ IDEA.
 * User: lamigud
 * Date: 6/15/13
 * Time: 10:15 PM
 * To change this template use File | Settings | File Templates.
 */
class Authentication extends Specification {

  "Hasher" should {
    "SHA512 hash 'example string'" in {
       "example string" sha512= "example string".sha512
    }
  }

}
