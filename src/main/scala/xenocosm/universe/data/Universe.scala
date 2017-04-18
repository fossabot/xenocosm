package xenocosm
package universe
package data

import java.nio.ByteBuffer
import java.util.UUID
import cats.{Eq, Show}
import spire.random.Dist
import spire.random.rng.BurtleRot2
import squants.space._

import xenocosm.geometry.data.Point3
import xenocosm.instances.interop._

final case class Universe(uuid:UUID, age:Long, diameter:Length)

object Universe {
  /** Interesting things stop happening after about 20B yrs
    * https://en.wikipedia.org/wiki/Ultimate_fate_of_the_universe
    */
  private val MAX_AGE      = 20000000000L

  /** Interesting things start happening after 9B yrs
    * https://en.wikipedia.org/wiki/Timeline_of_the_formation_of_the_Universe
    */
  private val MIN_AGE      =  9000000000L
  private val MAX_DIAMETER = 30000000000L
  private val MIN_DIAMETER = 10000000000L

  def galaxy(seed:Long, universe:Universe, loc:Point3):Option[Galaxy] = {
    val buffer:ByteBuffer =
      ByteBuffer.
        allocate(48).
        putLong(seed).
        putLong(universe.uuid.getMostSignificantBits).
        putLong(universe.uuid.getLeastSignificantBits).
        putDouble(loc.x.toParsecs).
        putDouble(loc.y.toParsecs).
        putDouble(loc.z.toParsecs)

    proof(buffer.array()) map { bytes ⇒
      val a:Int = ByteBuffer.wrap(Array[Byte](bytes.take(4):_*)).getInt
      val b:Int = ByteBuffer.wrap(Array[Byte](bytes.slice(4, 4):_*)).getInt
      val c:Int = ByteBuffer.wrap(Array[Byte](bytes.slice(8, 4):_*)).getInt
      val d:Int = ByteBuffer.wrap(Array[Byte](bytes.slice(12, 4):_*)).getInt
      Galaxy.dist(universe, loc)(BurtleRot2.create(a, b, c, d))
    }
  }

  trait Instances {

    implicit val universeHasShow:Show[Universe] = Show.show {
      (a:Universe) ⇒
        """Age: %4.2e yrs
          |Diameter: %s
          |""".stripMargin.format(
          a.age.toDouble,
          a.diameter.toString(Parsecs)
        )
    }

    implicit val universeHasEq:Eq[Universe] = Eq.fromUniversalEquals[Universe]
    implicit val universeHasDist:Dist[Universe] =
      for {
        uuid ← implicitly[Dist[UUID]]
        age ← Dist.gen(_.nextLong(MIN_AGE, MAX_AGE))
        diameter ← Dist.gen(_.nextLong(MIN_DIAMETER, MAX_DIAMETER))
      } yield Universe(uuid, age, Parsecs(diameter))
  }
  object instances extends Instances
}
