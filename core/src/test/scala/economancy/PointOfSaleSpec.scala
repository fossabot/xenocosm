package economancy

import java.time.Instant
import squants.market.Currency

class PointOfSaleSpec extends xenocosm.test.XenocosmSuite {
  import pointOfSale._

  object Zorkmid extends Currency("ZMD", "Zorkmid", "Z", 2)
  object FrobozzElectricLamp

  test("price") {
    implicit val pos:PointOfSale[FrobozzElectricLamp.type] =
      PointOfSale.const[FrobozzElectricLamp.type](Zorkmid(1))

    FrobozzElectricLamp.price(Instant.now()) == Zorkmid(1)
  }
}
