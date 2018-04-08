---
layout: docs
title: Universe API
---

# Universe API

## GET /{uuid}

Returns a Universe identified by a UUID in a URL-safe Base64 encoding with
padding removed. This will always be 22 characters in length.

Example encoding below.

```tut
import java.nio.ByteBuffer
import java.util.UUID
import java.util.Base64

val uuid = UUID.fromString("00000000-0000-0000-0000-000000000000")

// Pack the UUID into a ByteBuffer
val buffer = ByteBuffer.
               allocate(16).
               putLong(uuid.getMostSignificantBits).
               putLong(uuid.getLeastSignificantBits)

// b64 those bytes
Base64.getUrlEncoder.withoutPadding.encodeToString(buffer.array())
```

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
import xenocosm.http.data.UniverseResponse
import UniverseResponse.instances._

val universe = Universe(UUID.fromString("00000000-0000-0000-0000-000000000000"))
val origin = Point3(Parsecs(0), Parsecs(0), Parsecs(0))
val range = Parsecs(10000)
val response = UniverseResponse(universe, origin, range)
```

#### Request

```tut:passthrough
println(s"""```http
           |GET /${⎈(universe.uuid)} HTTP/1.1
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
