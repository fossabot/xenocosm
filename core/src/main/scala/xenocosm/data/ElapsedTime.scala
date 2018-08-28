package xenocosm
package data

import cats.kernel.{Eq, Monoid}
import squants.motion.{SpeedOfLight, Velocity}
import squants.time.{Seconds, Time}

final case class ElapsedTime(moving:Time, rest:Time)

object ElapsedTime {
  val zero:ElapsedTime = ElapsedTime(Seconds(0), Seconds(0))

  // the ratio of v to the speed of light c
  val β:Velocity => Double = _ / SpeedOfLight

  // The Lorentz Factor for a relative velocity
  val γ:Velocity => Double = v => 1 / Math.sqrt(1 - β(v) * β(v))

  def fromVelocity(velocity:Velocity, moving:Time):ElapsedTime =
    ElapsedTime(moving, moving * γ(velocity))

  trait Instances {
    implicit val elapsedTimeHasEq:Eq[ElapsedTime] =
      Eq.fromUniversalEquals[ElapsedTime]

    implicit val elapsedTimeHasMonoid:Monoid[ElapsedTime] =
      new Monoid[ElapsedTime] {
        def empty:ElapsedTime = ElapsedTime.zero
        def combine(x:ElapsedTime, y:ElapsedTime):ElapsedTime =
          ElapsedTime(x.moving + y.moving, x.rest + y.rest)
      }
  }
  object instances extends Instances
}
