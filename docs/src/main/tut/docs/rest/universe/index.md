---
layout: docs
title: Universe API
---

# Universe API

## GET /{uuid}

Returns a Universe identified by a UUID in the format 00000000-0000-0000-0000-000000000000.

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

#### Request

```http
GET /00000000-0000-0000-0000 HTTP/1.1
Host: xenocosm.com
Accept: application/hal+json
```

#### Response Headers

```http
HTTP/1.1 200 OK
Content-Type: application/hal+json; charset=UTF-8
Server: xenocosm/0.0.0
```

#### Response Body

```tut:invisible
import java.util.UUID
import io.circe.syntax._
import squants.space.Parsecs
import galaxique.data._
import xenocosm.http.data.UniverseResponse
import xenocosm.http.hal.universe._

val universe = Universe(UUID.fromString("00000000-0000-0000-0000-000000000000"))
val origin = Point3(Parsecs(0), Parsecs(0), Parsecs(0))
val range = Parsecs(10000)
val response = UniverseResponse(universe, origin, range)
```

```tut:passthrough
println(s"```json\n${response.asJson.toString}\n```")
```
