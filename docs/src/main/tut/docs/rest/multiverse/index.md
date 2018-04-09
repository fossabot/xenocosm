---
layout: docs
title: Multiverse API
---

# Multiverse API

## GET /v1/multiverse

Returns the multiverse. Limitless possibilities.

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
import io.circe.syntax._
import xenocosm.http.data.MultiverseResponse
import MultiverseResponse.instances._

val response = MultiverseResponse
```

#### Request

```http
GET /v1/multiverse HTTP/1.1
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

```tut:passthrough
println(s"""```json
           |${response.asJson.toString}
           |```""".stripMargin)
```
