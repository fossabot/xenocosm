package xenocosm

import squants.space.{AstronomicalUnits, Parsecs}

import xenocosm.http.data.{Point3Segment, UuidSegment}

package object http {
  val ♠ = UuidSegment
  val ♣ = Point3Segment(Parsecs(10000))
  val ♥ = Point3Segment(Parsecs(1))
  val ♦ = Point3Segment(AstronomicalUnits(1))
}
