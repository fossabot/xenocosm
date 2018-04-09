---
layout: docs
title: REST API
---

# Xenocosm REST API (v1)

## GET /v1

The root of the v1 REST API.

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
import xenocosm.http.data.ApiResponse
import ApiResponse.instances._

val response = ApiResponse(1)
```

#### Request

```http
GET /v1 HTTP/1.1
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
