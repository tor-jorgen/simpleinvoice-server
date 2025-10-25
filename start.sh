#!/bin/bash

help() {
  echo "start.sh [--no-daemon]"
  echo "Start Simple Invoice backend"
  echo "--no-daemon: Do not run containers as daemons. This makes the logs visible in the console"
  echo "--skip-build: Do not build the application. This can make startup faster if you have already run the application"
  echo "Run './stop.sh' to stop the backend. If you start the backend with --no-daemon, you need to stop it with Ctrl+C"
}

show_info() {
  echo "Simple Invoice App can be reached in a web browser at http://localhost"
  echo "Run './stop.sh' to stop Simple Invoice"
}

# Create the default config directory if it does not exist
create_config_dir() {
  ENV_FILE=".env"
  VAR_NAME="CFG_PATH"
  CFG_PATH=$(grep "^$VAR_NAME=" "$ENV_FILE" | cut -d '=' -f 2)
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

if [[ "$1" == "--no-daemon" || "$2" == "--no-daemon" ]]; then
  DAEMON=
  show_info
else
  DAEMON=-d
fi

if [[ "$1" == "--skip-build" || "$2" == "--skip-build" ]]; then
  echo "Skipping build"
else
  docker compose build
fi

docker compose up $DAEMON

if [ "$DAEMON" == "-d" ]; then
  echo
  show_info
fi
