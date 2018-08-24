package galaxique

import galaxique.data._
import org.scalacheck.Gen
import squants.space.Meters

package object gen {
  lazy val universe:Gen[Universe] = Gen.uuid.map(Universe.apply)

  lazy val point3:Gen[Point3] =
    for {
      x <- Gen.chooseNum(Double.MinValue, Double.MaxValue)
      y <- Gen.chooseNum(Double.MinValue, Double.MaxValue)
      z <- Gen.chooseNum(Double.MinValue, Double.MaxValue)
    } yield Point3(Meters(x), Meters(y), Meters(z))

  lazy val galaxy:Gen[Galaxy] =
    for {
      universe <- universe
      loc <- point3
    } yield Galaxy(universe, loc)

  lazy val star:Gen[Star] =
    for {
      galaxy <- galaxy
      loc <- point3
    } yield Star(galaxy, loc)

  lazy val planet:Gen[Planet] =
    for {
      star <- star
      loc <- point3
    } yield Planet(star, loc)

  lazy val morganKeenan:Gen[MorganKeenan] =
    Gen.oneOf(MorganKeenan.Observed.all)
}
