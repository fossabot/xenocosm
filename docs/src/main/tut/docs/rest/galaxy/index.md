---
layout: docs
title: Galaxy API
---

# Galaxy API

## GET /v1/multiverse/{uuid}/{loc}

Returns a Galaxy identified by its location within a universe.

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
import squants.space.Parsecs
import galaxique.data._
import xenocosm.http._
import xenocosm.http.response.GalaxyResponse
import GalaxyResponse.instances._

val universe = Universe(UUID.fromString("00000000-0000-0000-0000-000000000000"))
val galaxy = Galaxy(universe, Point3(Parsecs(-10000), Parsecs(-10000), Parsecs(0)))
val origin = Point3(Parsecs(0), Parsecs(0), Parsecs(0))
val range = Parsecs(1)
val response = GalaxyResponse(galaxy, origin, range)
```

#### Request

```tut:passthrough
println(s"""```http
           |GET /v1/multiverse/${⎈(universe.uuid)}/${✺(galaxy.loc)} HTTP/1.1
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
