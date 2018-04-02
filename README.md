# Xenocosm

[![Build Status](https://travis-ci.org/robotsnowfall/xenocosm.svg?branch=master)](https://travis-ci.org/robotsnowfall/xenocosm)
[![codecov](https://codecov.io/gh/robotsnowfall/xenocosm/branch/master/graph/badge.svg)](https://codecov.io/gh/robotsnowfall/xenocosm)

A turn-based, procedural space trader for HTTP.

More info in the [documentation](https://robotsnowfall.github.io/xenocosm/).

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
