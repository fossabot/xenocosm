# Xenocosm

[![Build Status](https://travis-ci.org/robotsnowfall/xenocosm.svg?branch=master)](https://travis-ci.org/robotsnowfall/xenocosm)
[![codecov](https://codecov.io/gh/robotsnowfall/xenocosm/branch/master/graph/badge.svg)](https://codecov.io/gh/robotsnowfall/xenocosm)

More info in the [documentation](https://robotsnowfall.github.io/xenocosm/).

## Run the HTTP service with Docker

```bash
docker pull robotsnowfall/xenocosm-http
docker run -d -p 8080:8080 robotsnowfall/xenocosm-http:latest
```

## Minimal client

```bash
#!/bin/bash

URL="http://127.0.0.1:8080${1}"
curl --include \
 --cookie xenocosm.txt \
 --cookie-jar xenocosm.txt \
 "${URL}"
```

## Implementors

* [Doug Hurst](https://github.com/robotsnowfall)
