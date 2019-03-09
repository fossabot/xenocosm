package pseudoglot

import pseudoglot.data._
import spire.random.Dist

/**
  * A procedural instance of `Transcription` which encodes tables and charts
  * found in _The Handbook of the International Phonetic Association_.
  */
object Romanization {
  import cats.implicits._

  private val simplePulmonics:Map[Pulmonic, List[String]] = Map(
    Pulmonic(Voiceless, Bilabial, Plosive)         -> List("p"),
    Pulmonic(Voiced, Bilabial, Plosive)            -> List("b"),
    Pulmonic(Voiced, Bilabial, Nasal)              -> List("m"),
    Pulmonic(Voiceless, LabioDental, Fricative)    -> List("f", "ph"),
    Pulmonic(Voiced, LabioDental, Fricative)       -> List("v"),
    Pulmonic(Voiceless, Dental, Fricative)         -> List("th"),
    Pulmonic(Voiced, Dental, Fricative)            -> List("th"),
    Pulmonic(Voiceless, Alveolar, Plosive)         -> List("t"),
    Pulmonic(Voiced, Alveolar, Plosive)            -> List("d"),
    Pulmonic(Voiced, Alveolar, Nasal)              -> List("n"),
    Pulmonic(Voiceless, Alveolar, Fricative)       -> List("s", "c"),
    Pulmonic(Voiceless, Alveolar, Fricative)       -> List("ts"),
    Pulmonic(Voiced, Alveolar, Fricative)          -> List("z"),
    Pulmonic(Voiced, Alveolar, Approximant)        -> List("r"),
    Pulmonic(Voiced, Alveolar, LateralApproximant) -> List("l", "ll"),
    Pulmonic(Voiceless, PostAlveolar, Fricative)   -> List("sh", "š"),
    Pulmonic(Voiced, PostAlveolar, Fricative)      -> List("zh", "ž"),
    Pulmonic(Voiced, Palatal, Approximant)         -> List("y"),
    Pulmonic(Voiceless, Velar, Plosive)            -> List("k", "c"),
    Pulmonic(Voiced, Velar, Plosive)               -> List("g"),
    Pulmonic(Voiced, Velar, Nasal)                 -> List("ng"),
    Pulmonic(Voiced, Velar, Approximant)           -> List("w"),
    Pulmonic(Voiceless, Glottal, Fricative)        -> List("h")
  )

  private val simpleVowels:Map[Vowel, List[String]] = Map(
    Vowel(Unrounded, Close, Front)                 -> List("ee", "ä"),
    Vowel(Unrounded, NearClose, NearFront)         -> List("i"),
    Vowel(Unrounded, CloseMid, Front)              -> List("ay", "ö"),
    Vowel(Unrounded, OpenMid, Front)               -> List("e"),
    Vowel(Unrounded, NearOpen, Front)              -> List("a"),
    Vowel(Unrounded, Open, Back)                   -> List("o"),
    Vowel(Rounded, CloseMid, Back)                 -> List("ow"),
    Vowel(Rounded, NearClose, NearBack)            -> List("oo"),
    Vowel(Rounded, Close, Back)                    -> List("ew"),
    Vowel(Unrounded, OpenMid, Back)                -> List("u"),
    Vowel(Unrounded, Mid, Central)                 -> List("u")
  )

  private val xscribeNull:Map[NullPhoneme.type, List[String]] =
    Map(NullPhoneme -> List("'", "-", " ", ""))

  private def mkDist[T <: Phone]: Map[T, List[String]] => Dist[Transcription] =
    xs => Dist.gen(gen => Transcription {
      xs.mapValues {
        case Nil => ""
        case y :: Nil => y
        case ys => gen.chooseFromSeq(ys)(gen)
      }
    })

  val dist: Dist[Transcription] =
    for {
      pulmonics <- mkDist(simplePulmonics)
      vowels <- mkDist(simpleVowels)
      nulls <- mkDist(xscribeNull)
    } yield pulmonics |+| vowels |+| nulls
}
