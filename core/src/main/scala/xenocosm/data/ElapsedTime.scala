package xenocosm
package data

import cats.kernel.{Eq, Monoid}
import squants.time.{Seconds, Time}

final case class ElapsedTime(local:Time, reference:Time)

object ElapsedTime {
  val zero:ElapsedTime = ElapsedTime(Seconds(0), Seconds(0))

  trait Instances {
    implicit val elapsedTimeHasEq:Eq[ElapsedTime] =
      Eq.fromUniversalEquals[ElapsedTime]

    implicit val elapsedTimeHasMonoid:Monoid[ElapsedTime] =
      new Monoid[ElapsedTime] {
        def empty:ElapsedTime = ElapsedTime.zero
        def combine(x:ElapsedTime, y:ElapsedTime):ElapsedTime =
          ElapsedTime(x.local + y.local, x.reference + y.reference)
      }
  }
  object instances extends Instances
}
