# Xenocosm

[![Build Status](https://api.travis-ci.org/robotsnowfall/xenocosm.svg)](https://travis-ci.org/robotsnowfall/xenocosm)
[![codecov.io](http://codecov.io/github/robotsnowfall/xenocosm/coverage.svg?branch=master)](http://codecov.io/github/robotsnowfall/xenocosm?branch=master)

A lonely-space sim for HTTP.

## Minimal client

```bash
curl -i https://xenocosm.com
```

## Extended Director's Cut GOTY client

```bash
#!/usr/bin/env bash

URL=$1
curl --include --cookie xenocosm.txt --cookie-jar xenocosm.txt https://xenocosm.com"${1}"
echo ""
```

## Implementors

* [Doug Hurst](https://github.com/robotsnowfall)
