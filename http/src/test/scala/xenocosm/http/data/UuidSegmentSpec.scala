package xenocosm.http
package data

import java.util.UUID
import org.http4s.dsl.impl._

class UuidSegmentSpec extends xenocosm.test.XenocosmSuite {

  test("UuidSegment.path.unapply") {
    val uuid1 = UUID.fromString("00000000-0000-0000-0000-000000000000")
    val path = Path("/00000000-0000-0000-0000-000000000000")
    path match {
      case Root / UuidSegment(uuid2) => uuid1 shouldBe uuid2
      case _ => fail()
    }
  }
}
