#!/bin/bash

help() {
  echo "start.sh [--no-daemon]"
  echo "Start Simple Invoice backend"
  echo "--no-daemon: Do not run containers as daemons. This makes the logs visible in the console"
  echo "Run './stop.sh' to stop the backend. If you start the backend with --no-daemon, you need to stop it with Ctrl+C"
}

if [[ "$1" == "-h" || "$1" == "--help" ]]; then
  help
  exit 0
fi

if [ "$1" == "--no-daemon" ]; then
  DAEMON=
else
  DAEMON=-d
fi

docker compose up $DAEMON
