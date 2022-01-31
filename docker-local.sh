#!/bin/sh

# This script runs (1) a gradle build then (2) build local docker image.  This script is designed for local dev.

set -e

./gradlew clean build
docker build . -t vf-starter

echo  Once built you can run [ docker-compose up ] to run VF-Starter on a local server.
