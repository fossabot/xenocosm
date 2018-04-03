#!/bin/bash
set -e

git config --global user.email "no-one@example.com"
git config --global user.name "Automated Publish"
git config --global push.default simple

sbt docs/publishMicrosite
