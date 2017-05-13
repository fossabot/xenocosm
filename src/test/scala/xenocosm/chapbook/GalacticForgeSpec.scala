package xenocosm
package chapbook

import java.util.UUID
import spire.random.rng.BurtleRot2
import squants.space.Parsecs

import xenocosm.geometry.data.Point3
import xenocosm.universe.data.Universe

class GalacticForgeSpec extends XenocosmSuite {

  test("stanzas are deterministic") {
    forAll { (a:Byte, b:Byte, c:Byte, d:Byte) ⇒
      val rng1 = BurtleRot2.fromBytes(Array(a, b, c, d))
      val rng2 = BurtleRot2.fromBytes(Array(a, b, c, d))

      GalacticForge.stanza(rng1) === GalacticForge.stanza(rng2)
    }
  }

  test("can generate stanza from byte array") {
    val bytes = Array(1.toByte, 2.toByte, 3.toByte, 4.toByte)

    GalacticForge.fromBytes(bytes) === GalacticForge.fromBytes(bytes)
  }

  test("can generate stanza from intergalactic space") {
    val universe = Universe(UUID.randomUUID())
    val loc = Point3(Parsecs(0), Parsecs(0), Parsecs(0))

    GalacticForge.fromIntergalacticSpace((universe, loc)) ===
      GalacticForge.fromIntergalacticSpace((universe, loc))
  }

}
