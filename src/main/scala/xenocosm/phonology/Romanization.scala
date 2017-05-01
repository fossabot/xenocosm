package xenocosm
package phonology

import spire.algebra.MetricSpace
import spire.syntax.metricSpace._
import xenocosm.phonology.data._
import xenocosm.phonology.data.Pulmonic.instances._
import xenocosm.phonology.data.Vowel.instances._

object Romanization {

  def closestIn[A](a:A, in:Int, map:Map[A, String])(implicit ms:MetricSpace[A, Int]):Option[String] =
    map.
      toVector.
      map({ case (key, value) ⇒ (key, value, key.distance[Int](a)) }).
      sortBy(_._3).
      headOption.
      filter(_._3 < in).
      map(_._2)

  /** A simple encoding of the American English tables and charts found in
    * _The Handbook of the International Phonetic Association_, p.41-44
    */
  val enUSPulmonics:Map[Pulmonic, String] = Map(
    Pulmonic(Voiceless, Bilabial, Plosive) → "p",
    Pulmonic(Voiced, Bilabial, Plosive) → "b",
    Pulmonic(Voiced, Bilabial, Nasal) → "m",
    Pulmonic(Voiceless, LabioDental, Fricative) → "f",
    Pulmonic(Voiced, LabioDental, Fricative) → "v",
    Pulmonic(Voiceless, Dental, Fricative) → "th",
    Pulmonic(Voiced, Dental, Fricative) → "th",
    Pulmonic(Voiceless, Alveolar, Plosive) → "t",
    Pulmonic(Voiced, Alveolar, Plosive) → "d",
    Pulmonic(Voiced, Alveolar, Nasal) → "n",
    Pulmonic(Voiceless, Alveolar, Fricative) → "s",
    Pulmonic(Voiced, Alveolar, Fricative) → "z",
    Pulmonic(Voiced, Alveolar, Approximant) → "r",
    Pulmonic(Voiced, Alveolar, LateralApproximant) → "l",
    Pulmonic(Voiceless, PostAlveolar, Fricative) → "sh",
    Pulmonic(Voiced, PostAlveolar, Fricative) → "zh",
    Pulmonic(Voiced, Palatal, Approximant) → "y",
    Pulmonic(Voiceless, Velar, Plosive) → "k",
    Pulmonic(Voiced, Velar, Plosive) → "g",
    Pulmonic(Voiced, Velar, Nasal) → "ng",
    Pulmonic(Voiced, Velar, Approximant) → "w",
    Pulmonic(Voiceless, Glottal, Fricative) → "h"
  )

  val enUSVowels:Map[Vowel, String] = Map[Vowel, String](
    Vowel(Unrounded, Close, Front) → "ee",
    Vowel(Unrounded, NearClose, NearFront) → "i",
    Vowel(Unrounded, CloseMid, Front) → "ay",
    Vowel(Unrounded, OpenMid, Front) → "e",
    Vowel(Unrounded, NearOpen, Front) → "a",
    Vowel(Unrounded, Open, Back) → "o",
    Vowel(Rounded, CloseMid, Back) → "ow",
    Vowel(Rounded, NearClose, NearBack) → "oo",
    Vowel(Rounded, Close, Back) → "ew",
    Vowel(Unrounded, OpenMid, Back) → "u",
    Vowel(Unrounded,Mid,Central) → "u"
  )

  val enUS:Phone ⇒ Option[String] = {
    case p:Pulmonic ⇒ enUSPulmonics.get(p).orElse(closestIn(p, app.config.romanization.tolerance, enUSPulmonics))
    case v:Vowel ⇒ enUSVowels.get(v).orElse(closestIn(v, app.config.romanization.tolerance, enUSVowels))
  }

  val default:Romanization = _.flatMap(x ⇒ enUS(x)).mkString

  trait Syntax {
    implicit class PhoneSeqOps(underlying:Seq[Phone])(implicit rom:Romanization) {
      def romanize:String = rom(underlying)
    }
  }
  object syntax extends Syntax
}
