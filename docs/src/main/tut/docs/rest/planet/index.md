---
layout: docs
title: Planet API
---

# Planet API

## GET /{uuid}/{loc}/{loc}/{loc}

Returns a Planet identified by its location within a star system.

### Prerequisites

There are no prerequisites for this request.

### Request parameters

This request takes no parameters

### Optional request headers

| Name   | Value                |
|:-------|:---------------------|
| Accept | application/hal+json |

### Request body

Do not supply a request body with this method.

### Example

```tut:invisible
import java.util.UUID
import io.circe.syntax._
import squants.space.{AstronomicalUnits, Parsecs}
import galaxique.data._
import xenocosm.http._
import xenocosm.http.data.PlanetResponse
import PlanetResponse.instances._

val universe = Universe(UUID.fromString("00000000-0000-0000-0000-000000000000"))
val galaxy = Galaxy(universe, Point3(Parsecs(-10000), Parsecs(-10000), Parsecs(0)))
val star = Star(galaxy, Point3(Parsecs(0), Parsecs(-1), Parsecs(0)))
val planet = Planet(star, Point3(AstronomicalUnits(-1), AstronomicalUnits(0), AstronomicalUnits(-1)))
val response = PlanetResponse(planet)
```

#### Request

```tut:passthrough
println(s"""```http
           |GET /${♠(universe.uuid)}/${♣(galaxy.loc)}/${♥(star.loc)}/${♦(planet.loc)} HTTP/1.1
           |Host: xenocosm.com
           |Accept: application/hal+json
           |```""".stripMargin)
```

#### Response Headers

```http
HTTP/1.1 200 OK
Content-Type: application/hal+json; charset=UTF-8
Server: xenocosm/0.0.0
```

#### Response Body

```tut:passthrough
println(s"""```json
           |${response.asJson.toString}
           |```""".stripMargin)
```
