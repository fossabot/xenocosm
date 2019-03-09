package pseudoglot
package data

import java.security.MessageDigest
import cats.Eq
import cats.data.NonEmptyList
import spire.random.{Dist, Generator}
import spire.random.rng.BurtleRot3

final case class Language(morphology:Morphology, onomastics:NonEmptyList[OnomasticRule]) { self =>
  lazy private val indexedRules = Language.normalIndex(onomastics)

  lazy val anyRule:Dist[OnomasticRule] = Language.normalDist(indexedRules)
  lazy val anyName:Dist[List[Phones]] = anyRule.flatMap(_.apply(morphology))

  def nameFor(meaning: String): List[Phones] = anyName(Language.wordGen(meaning))
}

object Language {
  import Phones.syntax._

  def normalIndex[A](xs: NonEmptyList[A]):NonEmptyList[(Double, A)] =
    xs.zipWithIndex.map({
      case (x, i) => (((i.toDouble / xs.size) * 2) - 1, x)
    })

  def normalDist[A](ixs: NonEmptyList[(Double, A)]): Dist[A] = Dist.gen {
    gen =>
      val g = gen.nextGaussian()
      ixs.filter(_._1 < g) match {
        case Nil => ixs.toList.minBy(_._1)._2
        case iys => iys.maxBy(_._1)._2
      }
  }

  private def wordSeed(meaning: String): Array[Byte] =
    MessageDigest
      .getInstance("MD5")
      .digest(meaning.getBytes("UTF-8"))

  private def wordGen(meaning: String): Generator =
    BurtleRot3.fromBytes(wordSeed(meaning))

  def transcribeName(name: List[Phones])(implicit script: Transcription): String =
    name
      .map(_.transcribe.dropWhile(_.toString == Transcription.empty(script)).capitalize)
      .mkString(" ")

  val dist:Dist[Language] =
    for {
      morphology <- Morphology.dist
      onomastics <- OnomasticRule.dist.pack(3).map(_.distinct.toList)
    } yield Language(morphology, NonEmptyList.fromListUnsafe(onomastics))

  trait Instances {
    implicit val languageHasEq:Eq[Language] = Eq.fromUniversalEquals[Language]
  }
  object instances extends Instances
}
