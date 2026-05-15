#!/bin/bash

mode -e

help() {
  echo "start.sh [OPTIONS]"
  echo
  echo "Start Simple Invoice"
  echo
  echo "Options:"
  echo "-d, --no-daemon:    Do not run containers as daemons. This makes the logs visible in the console"
  echo "-l, --local-images: This will build the images for server and app locally instead of downloading images from GitHub"
  echo "-b, --no-build:     Do not build the application. This will make Simple Invoice start up faster if you are using local images and have already built the images"
  echo "-c, --cache:        Use Docker cache, default is to disable cache to ensure everything is rebuilt"
  echo
  echo "If the startup fails, it might be because you have to little memory on your computer. Try to close other programs while you start up. Please not that building Simple Invoice locally consumes quite some resources"
  echo "Run './stop.sh' to stop Simple Invoice. If you started it with --no-daemon, you need to push Ctrl+C before you run './stop.sh'"
}

show_info() {
  echo "Simple Invoice App can be reached in a web browser at http://localhost:8000"
  echo "Run './stop.sh' to stop Simple Invoice"
}

# Create the default config directory if it does not exist
create_config_dir() {
  CFG_PATH=$(grep "^CONFIG_DIRECTORY=" ".env" | cut -d '=' -f 2)
  if [ "$CFG_PATH" == "" ]; then
    CFG_PATH=".config"
  fi

  if [ ! -d "$CFG_PATH" ]; then
    mkdir "$CFG_PATH"
    echo "Created config directory at: $CFG_PATH"
  else
    echo "Config directory: $CFG_PATH"
  fi
}

DAEMON=-d
COMPOSE_FILE=compose.yaml
NO_BUILD=False
LOCAL_IMAGES=False
NO_CACHE="--no-cache"

while [[ "$1" == "--"* || "$1" == "-"* ]]; do
  case $1 in
    --no-daemon|-d)
      DAEMON=
      show_info
      shift 1
      ;;
    --local-images|-l)
      LOCAL_IMAGES=True
      COMPOSE_FILE=compose-build.yaml
      shift 1
      ;;
    --no-build|-b)
      NO_BUILD=True
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

echo "Starting Simple Invoice"

create_config_dir

if [[ "$NO_BUILD" == "True" ]]; then
  echo "Skipping build..."
elif [[ "$LOCAL_IMAGES" == "True" ]]; then
  docker compose -f compose-build.yaml build "$NO_CACHE" --progress plain
fi

if [ -n "$DAEMON" ]; then
  docker compose -f "$COMPOSE_FILE"  --progress plain up "$DAEMON"
else
  docker compose -f "$COMPOSE_FILE" --progress plain up
fi

if [ "$DAEMON" == "-d" ]; then
  echo
  show_info
fi
