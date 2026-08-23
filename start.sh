#!/bin/bash

set -e

help() {
  echo "start.sh [OPTIONS]"
  echo
  echo "Start Simple Invoice"
  echo
  echo "OPTIONS:"
  echo "-d, --no-daemon:    Do not run containers as daemons. This makes the logs visible in the console"
  echo "-l, --local-images: This will build the images for server and app locally instead of downloading images from GitHub."
  echo "                    To start it faster the next time, just start it without this option"
  echo "-c, --cache:        Use Docker cache when building images locally. Ddefault is to disable cache to ensure everything is rebuilt"
  echo "-h, --help:         Show help"
  echo
  echo "If the startup fails, it might be because you have to little memory on your computer. Try to close other programs while you start up."
  echo "Please not that building Simple Invoice locally consumes quite some resources"
  echo "Run './stop.sh' to stop Simple Invoice. If you started it with --no-daemon, you need to push Ctrl+C before you run './stop.sh'"
}

DAEMON=-d
COMPOSE_FILE=compose.yaml
LOCAL_IMAGES=False
NO_CACHE=True

while [[ "$1" == "--"* || "$1" == "-"* ]]; do
  case $1 in
    --no-daemon|-d)
      DAEMON=
      shift 1
      ;;
    --local-images|-l)
      LOCAL_IMAGES=True
      shift 1
      ;;
    --cache|-c)
      NO_CACHE=False
      shift 1
      ;;
    --help|-h)
      help
      exit 0
      ;;
    *)
      echo "Unknown option: $1"
      echo
      help
      exit 1
      ;;
  esac
done

if [[ ! -f .env ]]; then
    echo "ERROR: You must create .env first, see README.md."
    exit 1
fi

if [[ "$LOCAL_IMAGES" == "True" ]]; then
  if [[ "$NO_CACHE" == "True" ]]; then
  echo "Building images locally without cache..."
    docker compose --progress plain -f "$COMPOSE_FILE" build --no-cache
  else
  echo "Building images locally with cache..."
    docker compose --progress plain -f "$COMPOSE_FILE" build
  fi
fi

echo "Starting Simple Invoice"
echo

source .env

if [ -z "$APP_PORT" ]; then
  APP_PORT=8000
fi

if [ -n "$DAEMON" ]; then
  docker compose --progress plain -f "$COMPOSE_FILE" up "$DAEMON"
  echo
  echo "Simple Invoice App can be reached in a web browser at http://localhost:$APP_PORT"
  echo "Run './stop.sh' to stop Simple Invoice"
else
  echo
  echo "Simple Invoice App can be reached in a web browser at http://localhost:$APP_PORT"
  echo "Press Ctrl+C, and then run './stop.sh' to stop Simple Invoice"
  echo
  docker compose --progress plain -f "$COMPOSE_FILE" up
fi
