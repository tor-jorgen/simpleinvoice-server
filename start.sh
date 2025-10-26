#!/bin/bash

help() {
  echo "start.sh [--no-daemon]"
  echo "Start Simple Invoice"
  echo "--no-daemon: Do not run containers as daemons. This makes the logs visible in the console"
  echo "--skip-build: Do not build the application. This will make startup faster if you have already run the application"
  echo
  echo "If the startup fails, it might be because you have to little memory on your computer. Try to close other programs while you start up. Simple Invoice has to be built the first time you start up, and that consumes quite some resources"
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
