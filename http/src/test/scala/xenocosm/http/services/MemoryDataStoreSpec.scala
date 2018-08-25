package xenocosm.http
package services

class MemoryDataStoreSpec extends xenocosm.test.XenocosmFunSuite {

  test("Identity CRUD operations") {
    val data = new MemoryDataStore()
    val identity = xenocosm.gen.identity.sample.get
    val foreignID = xenocosm.gen.foreignID.sample.get

    data.selectIdentity(identity.uuid).unsafeRunSync() shouldBe None

    data.createIdentity(identity).unsafeRunSync()
    data.selectIdentity(identity.uuid).unsafeRunSync() shouldBe Some(identity)

    val updated = identity.copy(ref = Some(foreignID))
    data.updateIdentity(updated).unsafeRunSync()
    data.selectIdentity(identity.uuid).unsafeRunSync() shouldBe Some(updated)
    data.selectIdentity(foreignID).unsafeRunSync() shouldBe Some(updated)

    data.deleteIdentity(identity).unsafeRunSync()
    data.selectIdentity(identity.uuid).unsafeRunSync() shouldBe None
  }

  test("Trader CRUD operations") {
    val data = new MemoryDataStore()
    val trader = xenocosm.gen.trader.sample.get
    val ship = xenocosm.gen.ship.sample.get

    data.selectTrader(trader.uuid).unsafeRunSync() shouldBe None

    data.createTrader(trader).unsafeRunSync()
    data.selectTrader(trader.uuid).unsafeRunSync() shouldBe Some(trader)

    val updated = trader.copy(ship = ship)
    data.updateTrader(updated).unsafeRunSync()
    data.selectTrader(trader.uuid).unsafeRunSync() shouldBe Some(updated)

    data.deleteTrader(trader).unsafeRunSync()
    data.selectTrader(trader.uuid).unsafeRunSync() shouldBe None
  }
}
