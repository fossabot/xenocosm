package galaxique

import spire.random.Dist

class MorganKeenanSpec extends xenocosm.test.XenocosmFunSuite {
  import MorganKeenan.instances._

  test("parse.classification.option") {
    forAll { (mk:MorganKeenan) ⇒
      MorganKeenan.fromClassification(mk.classification) shouldBe Some(mk)
    }
    MorganKeenan.fromClassification(' ') shouldBe None
  }

  test("parse.classification.either") {
    forAll { (mk:MorganKeenan) ⇒
      MorganKeenan.fromClassificationEither(mk.classification) shouldBe Right(mk)
    }
    MorganKeenan.fromClassificationEither(' ') shouldBe Left("Unsupported Morgan-Keenan classification ' '.")
  }

  test("dist.from.unbounded.intervals") {
    MorganKeenan.Observed.W.distTemperature shouldBe a[Dist[_]]
    MorganKeenan.Observed.W.distMass shouldBe a[Dist[_]]
    MorganKeenan.Observed.O.distRadius shouldBe a[Dist[_]]
    MorganKeenan.Observed.W.distLuminosity shouldBe a[Dist[_]]
    MorganKeenan.Observed.Y.distTemperature shouldBe a[Dist[_]]
    MorganKeenan.Observed.Y.distMass shouldBe a[Dist[_]]
    MorganKeenan.Observed.Y.distRadius shouldBe a[Dist[_]]
    MorganKeenan.Observed.Y.distLuminosity shouldBe a[Dist[_]]
  }
}
