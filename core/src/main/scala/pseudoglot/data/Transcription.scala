package pseudoglot
package data

import cats.Monoid
import cats.implicits._
import spire.algebra.MetricSpace
import spire.syntax.metricSpace._

object Transcription {
  import Phone.instances._

  def apply[T <: Phone](mapping:Map[T, String]):Transcription =
    mapping.map({ case (key, value) => List(key:Phone) -> value })

  private def pulmonics(xscript:Transcription):Map[Pulmonic, String] =
    xscript.foldLeft(Map.empty[Pulmonic, String])({
      case (acc, (List(k:Pulmonic), v)) => acc + (k -> v)
      case (acc, _) => acc
    })

  private def vowels(xscript:Transcription):Map[Vowel, String] =
    xscript.foldLeft(Map.empty[Vowel, String])({
      case (acc, (List(k:Vowel), v)) => acc + (k -> v)
      case (acc, _) => acc
    })

  def closestIn[T <: Phone](t:T, in:Int, mapping:Map[T, String])(implicit ms:MetricSpace[T, Int]):String =
    mapping.toVector
      .map({ case (k, v) ⇒ (k, v, k.distance[Int](t)) })
      .sortBy(_._3)
      .headOption
      .filter(_._3 < in)
      .map(_._2)
      .getOrElse("_")

  def transcribeWith(ts:List[Phone], xscript:Transcription):String =
    (ts, xscript.get(ts)) match {
      case (Nil, _) => ""
      case (_, Some(str)) => str
      case (NullPhoneme :: xs, None) => transcribeWith(xs, xscript)
      case ((x:Pulmonic) :: xs, None) => closestIn[Pulmonic](x, 3, pulmonics(xscript)) ++ transcribeWith(xs, xscript)
      case ((x:Vowel) :: xs, None) => closestIn[Vowel](x, 3, vowels(xscript)) ++ transcribeWith(xs, xscript)
    }

  trait Instances {
    implicit val transcriptionHasMonoid:Monoid[Transcription] =
      new Monoid[Transcription] {
        def empty:Transcription = Map.empty[List[Phone], String]
        def combine(x: Transcription, y: Transcription):Transcription = x ++ y
      }
  }
  object instances extends Instances
}
