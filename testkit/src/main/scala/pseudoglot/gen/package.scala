package pseudoglot

import cats.data.NonEmptyList
import org.scalacheck.Gen
import pseudoglot.data._

package object gen {
  lazy val backness:Gen[Backness] = Gen.oneOf(Backness.all)
  lazy val height:Gen[Height] = Gen.oneOf(Height.all)
  lazy val manner:Gen[Manner] = Gen.oneOf(Manner.all)
  lazy val place:Gen[Place] = Gen.oneOf(Place.all)
  lazy val roundedness:Gen[Roundedness] = Gen.oneOf(Roundedness.all)
  lazy val voicing:Gen[Voicing] = Gen.oneOf(Voicing.all)

  lazy val pulmonic:Gen[Pulmonic] =
    for {
      v <- voicing
      p <- place
      m <- manner
    } yield Pulmonic(v, p, m)

  lazy val vowel:Gen[Vowel] =
    for {
      r <- roundedness
      h <- height
      b <- backness
    } yield Vowel(r, h, b)

  lazy val phone:Gen[Phone] = Gen.oneOf(pulmonic, vowel)
  lazy val phones:Gen[Phones] =
    for {
      head <- phone
      tails <- Gen.listOf(phone)
    } yield NonEmptyList(head, tails)

  private lazy val transcriptionElement:Gen[(Phones, String)] =
    for {
      phones <- phones
      str <- Gen.alphaNumStr
    } yield phones -> str

  lazy val transcription:Gen[Transcription] = Gen.mapOf(transcriptionElement)

  private lazy val ruleAbstract:Gen[PhonotacticRule] = Gen.oneOf(Empty, AnyPulmonic, AnyVowel)
  private lazy val rulePulmonic:Gen[PhonotacticRule] = pulmonic.map(Literal.apply)
  private lazy val ruleVowel:Gen[PhonotacticRule] = vowel.map(Literal.apply)
  private lazy val rulePhone:Gen[PhonotacticRule] =  Gen.oneOf(ruleAbstract, rulePulmonic, ruleVowel)

  private lazy val ruleConcat:Gen[PhonotacticRule] =
    for {
      lhs <- rulePhone
      rhs <- rulePhone
    } yield PhonotacticRule.minimize(Concat(lhs, rhs))

  private lazy val ruleChoose:Gen[PhonotacticRule] =
    for {
      lhs <- rulePhone
      rhs <- rulePhone
    } yield PhonotacticRule.minimize(Choose(lhs, rhs))

  lazy val phonotacticRule:Gen[PhonotacticRule] = Gen.oneOf(rulePhone, ruleConcat, ruleChoose)
  lazy val phonotacticRules:Gen[PhonotacticRules] = Gen.nonEmptyListOf(phonotacticRule).map(NonEmptyList.fromListUnsafe)

  lazy val phonology:Gen[Phonology] =
    for {
      pulmonics <- Gen.nonEmptyListOf(pulmonic).map(NonEmptyList.fromListUnsafe)
      vowels <- Gen.nonEmptyListOf(vowel).map(NonEmptyList.fromListUnsafe)
      phonotacticRules <- phonotacticRules
    } yield Phonology(pulmonics, vowels, phonotacticRules)
}
