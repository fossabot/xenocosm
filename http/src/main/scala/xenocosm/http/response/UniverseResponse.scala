package xenocosm.http
package response

import galaxique.data.{Galaxy, Point3, Universe}
import galaxique.implicits._
import io.circe._
import spire.random.Dist
import squants.space.{Length, Parsecs}

final case class UniverseResponse(universe:Universe, loc:Point3, range:Length) {
  lazy val galaxies:Iterator[Galaxy] = universe.nearby(loc, range)
}

object UniverseResponse {
  trait Instances {
    import galaxique.json.point3._
    import galaxique.json.universe._
    import interop.squants.json.instances._
    import io.circe.syntax._

    implicit val universeResponseHasDist:Dist[UniverseResponse] =
      Dist[Universe].map(universe => UniverseResponse(universe, Point3.zero.in(Parsecs), Universe.scale))

    implicit val universeResponseHasJsonEncoder:Encoder[UniverseResponse] =
      Encoder.instance(res => Json.obj(
        "_links" -> Json.obj(
          "self" -> Json.obj("href" -> s"$apiMultiverse/${⎈(res.universe.uuid)}".asJson),
          "curies" -> Json.arr(apiCurie),
          "api:galaxy" -> res.galaxies.map({galaxy => Json.obj("href" ->
            s"$apiMultiverse/${⎈(galaxy.universe.uuid)}/${✺(galaxy.loc)}".asJson
          )}).toSeq.asJson
        ),
        "universe" -> res.universe.asJson,
        "loc" -> res.loc.asJson,
        "scan-range" -> res.range.asJson
      ))

    implicit val universeResponseHasJsonDecoder:Decoder[UniverseResponse] =
      Decoder.instance { hcur =>
        for {
          universe <- hcur.downField("universe").as[Universe]
          loc <- hcur.downField("loc").as[Point3]
          range <- hcur.downField("scan-range").as[Length]
        } yield UniverseResponse(universe, loc, range)
      }
  }
  object instances extends Instances
}
