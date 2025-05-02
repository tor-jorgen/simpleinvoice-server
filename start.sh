#!/bin/bash

./gradlew clean build buildFatJar --no-daemon
#docker build --no-cache .
docker compose build --no-cache

if [ "$1" == "--no-daemon" ]; then
  DAEMON=
else
  DAEMON=-d
fi

#docker compose up $DAEMON
docker compose up
