# Xenocosm

[![Build Status](https://api.travis-ci.org/robotsnowfall/xenocosm.svg)](https://travis-ci.org/robotsnowfall/xenocosm)
[![Code Coverage](http://codecov.io/github/robotsnowfall/xenocosm/coverage.svg?branch=master)](http://codecov.io/github/robotsnowfall/xenocosm?branch=master)

A turn-based, procedural space trader for HTTP.

## Minimal client

```bash
#!/bin/bash

URL="http://127.0.0.1:8080${1}"
curl --include \
 --cookie xenocosm.txt \
 --cookie-jar xenocosm.txt \
 "${URL}"
```

## TODO

- [ ] Traverse the multiverse
- [ ] Track multiversal time and subjective time
- [ ] Buy/sell cargo
- [ ] Game ends at multiversal `Instant.MAX`

## Implementors

* [Doug Hurst](https://github.com/robotsnowfall)
