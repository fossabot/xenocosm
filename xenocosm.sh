#!/usr/bin/env bash

URL=$1
curl --include --cookie xenocosm.txt --cookie-jar xenocosm.txt "${1}"
echo ""
