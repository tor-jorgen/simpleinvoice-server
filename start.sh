#!/bin/bash

help() {
  echo "start.sh [--no-daemon] [--local-images] [--skip-build]"
  echo "Start Simple Invoice"
  echo "--no-daemon: Do not run containers as daemons. This makes the logs visible in the console"
  echo "--local-images: This will build the images for server and app locally instead of using the prebuilt images from GitHub"
  echo "--skip-build: Do not build the application. This will make Simple Invoice start up faster if you are using local images and have already built them"
  echo
  echo "If the startup fails, it might be because you have to little memory on your computer. Try to close other programs while you start up. Please not that building Simple Invoice locally consumes quite some resources"
  echo "Run './stop.sh' to stop the backend. If you started it with --no-daemon, you need to push Ctrl+C before you run './stop.sh'"
}

show_info() {
  echo "Simple Invoice App can be reached in a web browser at http://localhost:8000"
  echo "Run './stop.sh' to stop Simple Invoice"
}

# Create the default config directory if it does not exist
create_config_dir() {
  CFG_PATH=$(grep "^CFG_PATH=" ".env" | cut -d '=' -f 2)
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

if [[ "$1" == "-h" || "$1" == "--help" ]]; then
  help
  exit 0
fi

create_config_dir

if [[ "$1" == "--no-daemon" || "$2" == "--no-daemon" || "$3" == "--no-daemon" ]]; then
  DAEMON=
  show_info
else
  DAEMON=-d
fi

if [[ "$1" == "--skip-build" || "$2" == "--skip-build" || "$3" == "--skip-build" ]]; then
  echo "Skipping build"
elif [[ "$1" == "--local-images" || "$2" == "--local-images" || "$3" == "--local-images" ]]; then
  docker compose -f compose-build.yaml build --no-cache
fi

if [[ "$1" == "--local-images" || "$2" == "--local-images" || "$3" == "--local-images" ]]; then
  COMPOSE_FILE=compose-build.yaml
else
  COMPOSE_FILE=compose.yaml
fi

docker compose -f "$COMPOSE_FILE" up $DAEMON

if [ "$DAEMON" == "-d" ]; then
  echo
  show_info
fi
