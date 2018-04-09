---
layout: docs
title: Star API
---

# Star API

## GET /v1/multiverse/{uuid}/{loc}/{loc}

Returns a Star identified by its location within a galaxy.

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
import xenocosm.http.data.StarResponse
import StarResponse.instances._

val universe = Universe(UUID.fromString("00000000-0000-0000-0000-000000000000"))
val galaxy = Galaxy(universe, Point3(Parsecs(-10000), Parsecs(-10000), Parsecs(0)))
val star = Star(galaxy, Point3(Parsecs(0), Parsecs(-1), Parsecs(0)))
val origin = Point3(AstronomicalUnits(0), AstronomicalUnits(0), AstronomicalUnits(0))
val range = AstronomicalUnits(1)
val response = StarResponse(star, origin, range)
```

#### Request

```tut:passthrough
println(s"""```http
           |GET /v1/multiverse/${⎈(universe.uuid)}/${✺(galaxy.loc)}/${✨(star.loc)} HTTP/1.1
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
