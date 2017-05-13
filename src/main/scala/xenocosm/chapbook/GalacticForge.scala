package xenocosm
package chapbook

import cats.implicits._
import spire.ClassTag
import spire.random.Generator
import spire.random.rng.BurtleRot2
import squants.space.Parsecs

import xenocosm.chapbook.data.Stanza
import xenocosm.geometry.data.Point3
import xenocosm.universe.data.Universe

/** An implementation of "Taroko Gorge" by Nick Montfort
  *
  * @see http://nickm.com/taroko_gorge/
  */
object GalacticForge {

  private val nounsTheory:Vector[String] = Vector(
    "quintessence", "eschatology", "cosmology", "dark matter", "dark energy",
    "cosmological constant", "orbifold", "monopole", "dark halo"
  )

  private val nounsObserved:Vector[String] = Vector(
    "galaxy", "redshift", "galactic corona", "red sequence", "speed of light",
    "electron", "blue cloud", "proton", "hydrogen", "helium", "baryon", "plasma"
  )

  private val verbsTransitive:Vector[String] = Vector(
    "span", "lie along", "occupy", "cool", "frame", "evolve", "exercise",
    "inform", "give rise to", "create", "illuminate", "stand alone with"
  )

  private val verbsImperative:Vector[String] = Vector(
    "accelerate", "quench", "polarize", "reveal", "cherish", "revel in",
    "quiver in ecstasy at"
  )

  private val verbsIntransitive:Vector[String] = Vector(
    "radiate", "merge", "lens", "cluster", "spark", "glint", "coruscate"
  )

  private val adjs:Vector[String] = Vector(
    "rarefied", "celestial", "cyclic", "ekpyrotic", "homogeneous", "isotropic",
    "thermodynamic", "glorious", "rascal", "undreamt", "lonely"
  )

  private val adjsTemperature:Vector[String] = Vector(
    "hot", "warm", "cold"
  )

  private val adjsGalacticShape:Vector[String] = Vector(
    "elliptical", "lenticular", "spiral"
  )

  private val phrases:Vector[String] = Vector(
    "you tire of all this beauty.",
    "they had faith in you, and you never came.",
    "a god flips the hourglass."
  )

  private val pluralize:String ⇒ String = {
    case "galactic corona" ⇒ "galactic coronae"
    case "lie along" ⇒ "lies along"
    case "speed of light" ⇒ "speeds of light"
    case "give rise to" ⇒ "gives rise to"
    case "stand alone with" ⇒ "stands alone with"
    case xs if xs.endsWith("s") ⇒ s"${xs}es"
    case xs if xs.endsWith("y") ⇒ xs.dropRight(1) ++ "ies"
    case xs ⇒ s"${xs}s"
  }

  private val definite:String ⇒ String = "the " ++ _

  private val indefinite:String ⇒ String = {
    case xs if xs.headOption.exists(x ⇒ "aeiou".contains(x)) ⇒ s"an $xs"
    case xs ⇒ s"a $xs"
  }

  private def choose[A](as:IndexedSeq[A]):Generator ⇒ A = gen ⇒ gen.chooseFromSeq(as)(gen)

  private def agree[A](f:A ⇒ A):Generator ⇒ (A, A) ⇒ (A, A) =
    _.oneOf[(A, A) ⇒ (A, A)](
      { case (a, b) ⇒ (f(a), b) },
      { case (c, d) ⇒ (c, f(d)) }
    )

  private def agree[A](f:A ⇒ A, xs:IndexedSeq[A], ys:IndexedSeq[A]):Generator ⇒ (A, A) =
    for {
      x ← choose(xs)
      y ← choose(ys)
      agreement ← agree(f)
    } yield agreement(x, y)

  private def sampler[A:ClassTag]:Generator ⇒ Traversable[A] ⇒ Traversable[A] =
    gen ⇒ xs ⇒ gen.sampleFromTraversable(xs, Math.min(gen.nextInt(3) + 2, xs.size))(implicitly[ClassTag[A]], gen)

  private val prefixer:Generator ⇒ String ⇒ String =
    _.nextInt(2) match {
      case 0 ⇒ definite
      case 1 ⇒ indefinite
    }

  private val foam:Generator ⇒ String =
    agree(pluralize, nounsObserved, verbsTransitive) flatMap {
      case ("galaxy", _) ⇒ choose(phrases)
      case (subject, verb) ⇒ for {
        directObject ← choose(nounsTheory)
        prefix ← prefixer
        affix ← choose(Vector(prefix, pluralize))
      } yield s"$subject $verb ${affix(directObject)}."
    }

  private val void:Generator ⇒ String =
    agree(pluralize, nounsTheory, verbsIntransitive) map { case (s, v) ⇒ s"$s $v." }

  private val filament:Generator ⇒ String =
    for {
      verb ← choose(verbsImperative)
      temperature ← choose(adjsTemperature)
      galacticShape ← choose(adjsGalacticShape)
      prefix ← prefixer
      sample ← sampler[String]
      whittled = sample(adjs ++ Vector(temperature, galacticShape))
    } yield s" $verb ${prefix(whittled.mkString(" "))} —"

  val stanza:Generator ⇒ Stanza = gen ⇒ {
    def loop(acc:Vector[String], n:Int, paths:Int):Vector[String] =
      n match {
        case 0 ⇒ loop(acc :+ foam(gen), n + 1, 1 + gen.nextInt(3))
        case _ if n < paths ⇒ loop(acc :+ void(gen), n + 1, paths)
        case _ if n == paths ⇒ loop(acc :+ foam(gen), n + 1, paths)
        case _ if n == paths + 1 ⇒ loop(acc ++ Vector("", filament(gen)), n + 1, paths)
        case _ ⇒ acc
      }

    Stanza(loop(Vector.empty[String], 0, 0) map (_.capitalize))
  }

  val fromBytes:Array[Byte] ⇒ Stanza = Digest.md5 andThen BurtleRot2.fromBytes andThen stanza

  val fromIntergalacticSpace:((Universe, Point3)) ⇒ Stanza =
    (Universe.bytes split Point3.bytes(Parsecs)).
      map({ case (xs, ys) ⇒ fromBytes(xs ++ ys) })

}
