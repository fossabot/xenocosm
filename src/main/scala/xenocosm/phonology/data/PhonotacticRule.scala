package xenocosm
package phonology
package data

import scala.annotation.tailrec
import cats.{Eq, Show}
import spire.random.Dist

sealed trait PhonotacticRule extends Product with Serializable
case object Empty extends PhonotacticRule
case object AnyPulmonic extends PhonotacticRule
case object AnyVowel extends PhonotacticRule
final case class LiteralPulmonic(pulmonic: Pulmonic) extends PhonotacticRule
final case class LiteralVowel(vowel: Vowel) extends PhonotacticRule
final case class Choose(xs:List[PhonotacticRule]) extends PhonotacticRule
final case class Concat(xs:List[PhonotacticRule]) extends PhonotacticRule

object PhonotacticRule {

  def consonantsDist(pulmonics:Seq[Pulmonic]):Dist[PhonotacticRule] = Dist.gen { gen ⇒
    @tailrec
    def loop(acc:PhonotacticRule):PhonotacticRule = (acc, gen.nextBoolean()) match {
      case (Empty, true) ⇒ loop(AnyPulmonic)
      case (AnyPulmonic, true) ⇒ loop(LiteralPulmonic(pulmonics(gen.nextInt(pulmonics.length))))
      case (x:LiteralPulmonic, true) ⇒ loop(Choose(List(AnyPulmonic, x)))
      case (Choose(xs), true)  ⇒ loop(Concat(xs))
      case (Concat(xs), true) ⇒ Concat(LiteralPulmonic(pulmonics(gen.nextInt(pulmonics.length))) :: xs)
      case _ ⇒ acc
    }

    loop(Empty)
  }

  def vowelsDist(vowels:Seq[Vowel]):Dist[PhonotacticRule] = Dist.gen { gen ⇒
    @tailrec
    def loop(acc:PhonotacticRule):PhonotacticRule = (acc, gen.nextBoolean()) match {
      case (AnyVowel, true) ⇒ loop(LiteralVowel(vowels(gen.nextInt(vowels.length))))
      case (x:LiteralVowel, true) ⇒ loop(Choose(List(AnyVowel, x)))
      case _ ⇒ acc
    }

    loop(AnyVowel)
  }

  private def show(acc:List[String], rule:PhonotacticRule):List[String] =
    rule match {
      case Empty ⇒ acc
      case AnyPulmonic ⇒ "C" :: acc
      case AnyVowel ⇒ "V" :: acc
      case LiteralPulmonic(c) ⇒ IPA.pulmonics.getOrElse(c, "") :: acc
      case LiteralVowel(v) ⇒ IPA.vowels.getOrElse(v, "") :: acc
      case Concat(rules) ⇒ rules.foldLeft(acc)(show)
      case Choose(rules) ⇒ ")" :: (rules.foldLeft(List.empty[String])(show) mkString "|") :: "(" :: acc
    }

  @tailrec
  private def simplify(rules:List[PhonotacticRule]):List[PhonotacticRule] =
    rules.map(simplify).filterNot(_ == Empty) match {
      case Nil ⇒ Nil
      case Empty :: Nil ⇒ Nil
      case x :: Nil ⇒ List(x)
      case Concat(ys) :: Concat(zs) :: tail ⇒ simplify(Concat(ys ++ zs) :: tail)
      case Concat(ys) :: y :: tail ⇒ simplify(Concat(ys ++ List(y)) :: tail)
      case y :: Concat(ys) :: tail ⇒ simplify(Concat(y :: ys) :: tail)
      case xs ⇒ xs
    }

  // Try to collapse generated rules into a canonical form
  def simplify(rule:PhonotacticRule):PhonotacticRule =
    rule match {
      case Concat(Nil) ⇒ Empty
      case Concat(x :: Nil) ⇒ simplify(x)
      case Concat(xs) ⇒ simplify(xs) match {
        case Nil ⇒ Empty
        case x :: Nil ⇒ x
        case ys ⇒ Concat(ys)
      }
      case Choose(Nil) ⇒ Empty
      case Choose(x :: Nil) ⇒ simplify(x)
      case Choose(xs) ⇒ simplify(xs) match {
        case Nil ⇒ Empty
        case x :: Nil ⇒ x
        case ys ⇒ Choose(ys)
      }
      case x ⇒ x
    }

  def syllable(phonology:Phonology):Dist[Vector[Phone]] =
    Dist.gen { gen ⇒

      def loop(acc:List[Phone], rule:PhonotacticRule):List[Phone] =
        rule match {
          case Empty ⇒ acc
          case AnyPulmonic ⇒ phonology.pulmonics(gen.nextInt(phonology.pulmonics.length)) :: acc
          case AnyVowel ⇒ phonology.vowels(gen.nextInt(phonology.vowels.length)) :: acc
          case LiteralPulmonic(x) ⇒ x :: acc
          case LiteralVowel(x) ⇒ x :: acc
          case Choose(xs) ⇒ loop(acc, xs(gen.nextInt(xs.length)))
          case Concat(xs) ⇒ xs.flatMap(loop(acc, _))
        }

      val phonotactics = phonology.phonotactics.toVector
      val phonotactic = phonotactics(gen.nextInt(phonotactics.length))
      loop(List.empty[Phone], phonotactic).toVector
    }

  def dist(pulmonics:Seq[Pulmonic], vowels:Seq[Vowel]):Dist[PhonotacticRule] =
    for {
      onset ← consonantsDist(pulmonics)
      nucleus ← vowelsDist(vowels)
      coda ← consonantsDist(pulmonics)
    } yield simplify(Concat(List(onset, nucleus, coda)))

  trait Instances {
    implicit val phonotacticRuleHasEq:Eq[PhonotacticRule] = Eq.fromUniversalEquals[PhonotacticRule]

    implicit val phonotacticRuleHasShow:Show[PhonotacticRule] =
      Show.show[PhonotacticRule](rule ⇒ show(List.empty[String], rule).reverse.mkString)
  }
  object instances extends Instances
}
