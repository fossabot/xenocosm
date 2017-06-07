#!/usr/bin/env bash

URL="http://127.0.0.1:8080${1}"
curl --include --cookie xenocosm.txt --cookie-jar xenocosm.txt "${URL}"
