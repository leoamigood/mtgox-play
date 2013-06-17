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
class AuthenticationTest extends Specification {

  "Hasher" should {
    "be able to hash using SHA512" in {
       "example string".sha512.toString mustEqual "f63ffbf293e2631e013dc2a0958f54f6797f096c36adda6806f717e1d4a314c0fb443ec71eec73cfbd8efa1ad2c709b902066e6356396b97a7ea5191de349012"
    }

    "be able to sign using SHA256 and a secret key" in {
      ("example string" hmacSha256 ("secret key")).toString mustEqual "43535a1b91a94db8071999ff79428ea9fa832e72134b629659884d66c5459613"
    }

    "be able to sign using SHA512 and a secret key as byte array" in {
      ("example string" hmacSha512 ("secret key".getBytes)).toString mustEqual "749a9c3223bf909b5b4ff170430d0770d2e2e21a2b2f99722091b3e32c7a20b77e9275d869f9468a3e6378753ee73e35621d58a3f08143c284a2c4fe2433c3af"
    }
  }

}
