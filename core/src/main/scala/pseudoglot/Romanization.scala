package pseudoglot

import pseudoglot.data._
import spire.random.Dist

/**
  * A procedural instance of `Transcription` which encodes tables and charts
  * found in _The Handbook of the International Phonetic Association_.
  */
object Romanization {
  import cats.implicits._

  private val simplePulmonics:Map[Pulmonic, String] = Map(
    Pulmonic(Voiceless, Bilabial, Plosive)         -> "p",
    Pulmonic(Voiced, Bilabial, Plosive)            -> "b",
    Pulmonic(Voiced, Bilabial, Nasal)              -> "m",
    Pulmonic(Voiceless, LabioDental, Fricative)    -> "f",
    Pulmonic(Voiced, LabioDental, Fricative)       -> "v",
    Pulmonic(Voiceless, Dental, Fricative)         -> "th",
    Pulmonic(Voiced, Dental, Fricative)            -> "th",
    Pulmonic(Voiceless, Alveolar, Plosive)         -> "t",
    Pulmonic(Voiced, Alveolar, Plosive)            -> "d",
    Pulmonic(Voiced, Alveolar, Nasal)              -> "n",
    Pulmonic(Voiceless, Alveolar, Fricative)       -> "s",
    Pulmonic(Voiced, Alveolar, Fricative)          -> "z",
    Pulmonic(Voiced, Alveolar, Approximant)        -> "r",
    Pulmonic(Voiced, Alveolar, LateralApproximant) -> "l",
    Pulmonic(Voiceless, PostAlveolar, Fricative)   -> "sh",
    Pulmonic(Voiced, PostAlveolar, Fricative)      -> "zh",
    Pulmonic(Voiced, Palatal, Approximant)         -> "y",
    Pulmonic(Voiceless, Velar, Plosive)            -> "k",
    Pulmonic(Voiced, Velar, Plosive)               -> "g",
    Pulmonic(Voiced, Velar, Nasal)                 -> "ng",
    Pulmonic(Voiced, Velar, Approximant)           -> "w",
    Pulmonic(Voiceless, Glottal, Fricative)        -> "h"
  )

  private val simpleVowels:Map[Vowel, String] = Map[Vowel, String](
    Vowel(Unrounded, Close, Front)                 -> "ee",
    Vowel(Unrounded, NearClose, NearFront)         -> "i",
    Vowel(Unrounded, CloseMid, Front)              -> "ay",
    Vowel(Unrounded, OpenMid, Front)               -> "e",
    Vowel(Unrounded, NearOpen, Front)              -> "a",
    Vowel(Unrounded, Open, Back)                   -> "o",
    Vowel(Rounded, CloseMid, Back)                 -> "ow",
    Vowel(Rounded, NearClose, NearBack)            -> "oo",
    Vowel(Rounded, Close, Back)                    -> "ew",
    Vowel(Unrounded, OpenMid, Back)                -> "u",
    Vowel(Unrounded, Mid, Central)                 -> "u"
  )

  private val base: Transcription = Transcription(simplePulmonics) |+| Transcription(simpleVowels)

  val dist: Dist[Transcription] = Dist.constant(base)
}
