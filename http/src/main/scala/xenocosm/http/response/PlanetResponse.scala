package xenocosm.http
package response

import galaxique.data.Planet
import io.circe._

final case class PlanetResponse(planet:Planet)

object PlanetResponse {
  trait Instances {
    import galaxique.json.planet._
    import io.circe.syntax._

    implicit val planetResponseHasJsonEncoder:Encoder[PlanetResponse] =
      Encoder.instance({ res =>
        val uuid = res.planet.star.galaxy.universe.uuid
        val locU = res.planet.star.galaxy.loc
        val locG = res.planet.star.loc
        val locS = res.planet.loc
        Json.obj(
          "_links" -> Json.obj(
            "self" -> Json.obj("href" -> s"$apiMultiverse/${⎈(uuid)}/${✺(locU)}/${✨(locG)}/${★(locS)}".asJson),
            "curies" -> Json.arr(apiCurie)
          ),
          "planet" -> res.planet.asJson
        )
      })

    implicit val planetResponseHasJsonDecoder:Decoder[PlanetResponse] =
      Decoder.instance { hcur =>
        for {
          planet <- hcur.downField("planet").as[Planet]
        } yield PlanetResponse(planet)
      }
  }
  object instances extends Instances
}
