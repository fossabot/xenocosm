package xenocosm.http
package response

import galaxique.data.{Planet, Point3, Star}
import galaxique.implicits._
import io.circe._
import spire.random.Dist
import squants.space.{AstronomicalUnits, Length}

final case class StarResponse(star:Star, loc:Point3, range:Length) {
  lazy val planets:Iterator[Planet] = star.nearby(loc, range)
}

object StarResponse {
  trait Instances {
    import galaxique.json.point3._
    import galaxique.json.star._
    import interop.squants.json.instances._
    import io.circe.syntax._

    implicit val starResponseHasDist:Dist[StarResponse] =
      Dist[Star].map(star => StarResponse(star, Point3.zero.in(AstronomicalUnits), Star.scale))

    implicit val starResponseHasJsonEncoder:Encoder[StarResponse] =
      Encoder.instance(res => Json.obj(
        "_links" -> Json.obj(
          "self" -> Json.obj("href" -> s"$apiMultiverse/${⎈(res.star.galaxy.universe.uuid)}/${✺(res.star.galaxy.loc)}/${✨(res.star.loc)}".asJson),
          "curies" -> Json.arr(apiCurie),
          "api:planet" -> res.planets.map(planet => Json.obj("href" ->
            s"$apiMultiverse/${⎈(planet.star.galaxy.universe.uuid)}/${✺(planet.star.galaxy.loc)}/${✨(planet.star.loc)}/${★(planet.loc)}".asJson
          )).toSeq.asJson
        ),
        "star" -> res.star.asJson,
        "loc" -> res.loc.asJson,
        "scan-range" -> res.range.asJson
      ))

    implicit val starResponseHasJsonDecoder:Decoder[StarResponse] =
      Decoder.instance { hcur =>
        for {
          star <- hcur.downField("star").as[Star]
          loc <- hcur.downField("loc").as[Point3]
          range <- hcur.downField("scan-range").as[Length]
        } yield StarResponse(star, loc, range)
      }
  }
  object instances extends Instances
}
