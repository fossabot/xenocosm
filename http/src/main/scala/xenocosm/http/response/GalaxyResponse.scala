package xenocosm.http
package response

import galaxique.data.{Galaxy, Point3, Star}
import galaxique.implicits._
import io.circe._
import squants.space.Length

final case class GalaxyResponse(galaxy:Galaxy, loc:Point3, range:Length) {
  lazy val stars:Iterator[Star] = galaxy.nearby(loc, range)
}

object GalaxyResponse {
  trait Instances {
    import galaxique.json.galaxy._
    import galaxique.json.point3._
    import interop.squants.json.instances._
    import io.circe.syntax._

    implicit val galaxyResponseHasJsonEncoder:Encoder[GalaxyResponse] =
      Encoder.instance(res => Json.obj(
        "_links" -> Json.obj(
          "self" -> Json.obj("href" -> s"$apiMultiverse/${⎈(res.galaxy.universe.uuid)}/${✺(res.galaxy.loc)}".asJson),
          "curies" -> Json.arr(apiCurie),
          "api:star" -> res.stars.map(star => Json.obj("href" ->
            s"$apiMultiverse/${⎈(star.galaxy.universe.uuid)}/${✺(star.galaxy.loc)}/${✨(star.loc)}".asJson
          )).toSeq.asJson
        ),
        "galaxy" -> res.galaxy.asJson,
        "loc" -> res.loc.asJson,
        "scan-range" -> res.range.asJson
      ))

    implicit val galaxyResponseHasJsonDecoder:Decoder[GalaxyResponse] =
      Decoder.instance { hcur =>
        for {
          galaxy <- hcur.downField("galaxy").as[Galaxy]
          loc <- hcur.downField("loc").as[Point3]
          range <- hcur.downField("scan-range").as[Length]
        } yield GalaxyResponse(galaxy, loc, range)
      }
  }
  object instances extends Instances
}
