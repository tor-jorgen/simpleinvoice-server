#!/bin/bash

help() {
  echo "start.sh [--list-env]"
  echo "Start Simple Invoice backend"
  echo "--list-env: List and export environment variables only"
}

list_environment_variables() {
  while read line || [ -n "$line" ]; do
    if [[ ! "$line" =~ ^JAVA_TOOL_OPTIONS.* ]]; then
      # Export environment variables, but replace internal Docker addresses with external
      EXPORT=$(echo "$line" | sed 's/host.docker.internal/localhost/')
      echo "$EXPORT"
      export "$EXPORT"
    fi
  done <.env
}

if [[ "$1" == "-h" || "$1" == "--help" ]]; then
  help
  exit 0
fi

if [[ "$1" == "--list-env" ]]; then
  list_environment_variables
  exit 0
fi

./gradlew clean build buildFatJar --no-daemon
docker compose build --no-cache

if [ "$1" == "--no-daemon" ]; then
  DAEMON=
else
  DAEMON=-d
fi

docker compose up $DAEMON
