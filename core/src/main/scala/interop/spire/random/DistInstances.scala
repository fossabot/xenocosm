package interop.spire
package random

import cats.Monad
import spire.random.Dist

trait DistInstances {
  implicit val distIsMonad:Monad[Dist] =
    new Monad[Dist] {
      def flatMap[A, B](fa: Dist[A])(f: A => Dist[B]): Dist[B] = fa.flatMap(f)

      def tailRecM[A, B](a: A)(f: A => Dist[Either[A, B]]): Dist[B] =
        f(a).flatMap({
          case Right(b) => pure(b)
          case Left(c) => tailRecM(c)(f)
        })

      def pure[A](x: A): Dist[A] = Dist.constant(x)
    }
}
