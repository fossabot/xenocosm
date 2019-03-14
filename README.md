# Xenocosm

[![GitHub tag](https://img.shields.io/github/tag/robotsnowfall/xenocosm.svg)](https://github.com/robotsnowfall/xenocosm/tags)
[![Build Status](https://travis-ci.org/robotsnowfall/xenocosm.svg?branch=master)](https://travis-ci.org/robotsnowfall/xenocosm)
[![codecov](https://codecov.io/gh/robotsnowfall/xenocosm/branch/master/graph/badge.svg)](https://codecov.io/gh/robotsnowfall/xenocosm)
[![Docker Pulls](https://img.shields.io/docker/pulls/robotsnowfall/xenocosm-http.svg)](https://hub.docker.com/r/robotsnowfall/xenocosm-http)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Frobotsnowfall%2Fxenocosm.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Frobotsnowfall%2Fxenocosm?ref=badge_shield)

More info in the [documentation](https://robotsnowfall.github.io/xenocosm/).

## Run the HTTP service with Docker

```bash
sbt docker:publishLocal
docker-compose up
```

## Connect to the HTTP service

[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/13b9b46acd596d72d7cd)

### Vanilla client

```bash
#!/bin/bash

URL="http://127.0.0.1:8080${1}"
curl --include \
     --location \
     --cookie xenocosm.txt \
     --cookie-jar xenocosm.txt \
     "${URL}"
echo
```

### Collector's Edition client (GOTY version)

```bash
#!/bin/bash

URL="http://127.0.0.1:8080${1}"
curl --silent \
     --location \
     --cookie xenocosm.txt \
     --cookie-jar xenocosm.txt \
     "${URL}" | jq
```

## Implementors

* [Doug Hurst](https://github.com/robotsnowfall)


## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Frobotsnowfall%2Fxenocosm.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Frobotsnowfall%2Fxenocosm?ref=badge_large)