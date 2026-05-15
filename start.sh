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
  echo "-c, --cache:        Use Docker cache, default is to disable cache to ensure everything is rebuilt"
  echo "-h, --help:         Show help"
  echo
  echo "If the startup fails, it might be because you have to little memory on your computer. Try to close other programs while you start up."
  echo "Please not that building Simple Invoice locally consumes quite some resources"
  echo "Run './stop.sh' to stop Simple Invoice. If you started it with --no-daemon, you need to push Ctrl+C before you run './stop.sh'"
}

DAEMON=-d
COMPOSE_FILE=compose.yaml
LOCAL_IMAGES=False
NO_CACHE="--no-cache"

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
      NO_CACHE=""
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
  echo "Building images locally..."
  docker compose -f "$COMPOSE_FILE" build "$NO_CACHE" --progress plain
fi

echo "Starting Simple Invoice"
echo

if [ -n "$DAEMON" ]; then
  docker compose -f "$COMPOSE_FILE"  --progress plain up "$DAEMON"
  echo
  echo "Simple Invoice App can be reached in a web browser at http://localhost:8000"
  echo "Run './stop.sh' to stop Simple Invoice"
else
  echo
  echo "Simple Invoice App can be reached in a web browser at http://localhost:8000"
  echo "Press Ctrl+C, and then run './stop.sh' to stop Simple Invoice"
  echo
  docker compose -f "$COMPOSE_FILE" --progress plain up
fi
