package pseudoglot
package data

import cats.Monoid
import cats.data.NonEmptyList
import cats.implicits._
import spire.algebra.MetricSpace
import spire.syntax.metricSpace._

object Transcription {
  import Phone.instances._

  def apply[T <: Phone](mapping:Map[T, String]):Transcription =
    mapping.map({ case (key, value) => NonEmptyList.one(key:Phone) -> value })

  private def pulmonics(xscript:Transcription):Map[Pulmonic, String] =
    xscript.foldLeft(Map.empty[Pulmonic, String])({
      case (acc, (NonEmptyList(k:Pulmonic, Nil), v)) => acc + (k -> v)
      case (acc, _) => acc
    })

  private def vowels(xscript:Transcription):Map[Vowel, String] =
    xscript.foldLeft(Map.empty[Vowel, String])({
      case (acc, (NonEmptyList(k:Vowel, Nil), v)) => acc + (k -> v)
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

  def transcribeWith(ts:Phones, xscript:Transcription):String =
    (ts, xscript.get(ts)) match {
      case (_, Some(str)) => str
      case (NonEmptyList(NullPhoneme, Nil), None) => ""
      case (NonEmptyList(NullPhoneme, x :: xs), None) => transcribeWith(NonEmptyList(x, xs), xscript)
      case (NonEmptyList(x:Pulmonic, Nil), None) => closestIn[Pulmonic](x, 3, pulmonics(xscript))
      case (NonEmptyList(x:Pulmonic, y :: ys), None) => closestIn[Pulmonic](x, 3, pulmonics(xscript)) ++ transcribeWith(NonEmptyList(y, ys), xscript)
      case (NonEmptyList(x:Vowel, Nil), None) => closestIn[Vowel](x, 3, vowels(xscript))
      case (NonEmptyList(x:Vowel, y :: ys), None) => closestIn[Vowel](x, 3, vowels(xscript)) ++ transcribeWith(NonEmptyList(y, ys), xscript)
    }

  trait Instances {
    implicit val transcriptionHasMonoid:Monoid[Transcription] =
      new Monoid[Transcription] {
        def empty:Transcription = Map.empty[Phones, String]
        def combine(x: Transcription, y: Transcription):Transcription = x ++ y
      }
  }
  object instances extends Instances
}
