package cavernetics

class FSMSpec extends xenocosm.test.XenocosmFunSuite {
  import FSM.syntax._

  sealed trait Event
  case object CoinInserted extends Event
  case object KnobTurned extends Event

  case class CandyMachine(locked: Boolean, candies: Int, coins: Int)

  implicit val fsm:FSM[CandyMachine, Event] = FSM[CandyMachine, Event] {
    case (machine, CoinInserted) if machine.locked && machine.candies > 0 =>
      machine.copy(locked = false, coins = machine.coins + 1)

    case (machine, KnobTurned) if !machine.locked && machine.candies > 0 =>
      machine.copy(locked = true, candies = machine.candies - 1)
  }

  test("can transition state") {
    val machine = CandyMachine(locked = true, 10, 0)

    machine.transition(CoinInserted).value shouldBe CandyMachine(locked = false, 10, 1)
  }

  test("invalid events result in no transition") {
    val machine = CandyMachine(locked = true, 10, 0)

    machine.transition(KnobTurned).value shouldBe machine
  }

  test("can sequence transitions") {
    val m0 = CandyMachine(locked = true, 10, 0)

    val mFinal = for {
      m1 <- m0.transition(CoinInserted)
      m2 <- m1.transition(KnobTurned)
      m3 <- m2.transition(CoinInserted)
      m4 <- m3.transition(KnobTurned)
      m5 <- m4.transition(CoinInserted)
      m6 <- m5.transition(KnobTurned)
      m7 <- m6.transition(CoinInserted)
      m8 <- m7.transition(KnobTurned)
      m9 <- m8.transition(CoinInserted)
      mX <- m9.transition(KnobTurned)
    } yield mX

    mFinal.value shouldBe CandyMachine(locked = true, 5, 5)

  }

  test("can transition a sequence") {
    val m0 = CandyMachine(locked = true, 10, 0)
    val events = List(CoinInserted,KnobTurned,CoinInserted,KnobTurned,CoinInserted,KnobTurned,CoinInserted,KnobTurned,CoinInserted,KnobTurned)

    val mFinal = m0.transition(events)

    mFinal.value shouldBe CandyMachine(locked = true, 5, 5)

  }

}
