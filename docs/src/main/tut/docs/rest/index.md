---
layout: docs
title: Multiverse API
---

# Multiverse API

## GET /

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

#### Request

```http
GET / HTTP/1.1
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
import io.circe.syntax._
import xenocosm.http.data.MultiverseResponse
import xenocosm.http.hal.multiverse._

val response = MultiverseResponse
```

```tut:passthrough
println(s"```json\n${response.asJson.toString}\n```")
```
