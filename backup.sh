#!/bin/bash

help() {
  echo "backup.sh [file name]"
  echo "Back up Simple Invoice database"
  echo "  file name: Name of file to backup to. Default is 'simpleinvoice.zip'"
  echo "Note! You need system administrator rights (sudo) to run this command, and you might have to enter your sudo password (depending on when you entered it the last time)"
}

if [[ "$1" == "-h" || "$1" == "--help" ]]; then
  help
  exit 0
fi

if [ "$1" == "" ]; then
  FILE=simpleinvoice.zip
else
  FILE="$1"
fi

DIR=$(docker volume inspect simple-invoice-db-data | sed -n 's/.*"Mountpoint": "\([^"]*\)".*/\1/p')
sudo zip -r "$FILE" "$DIR" >/dev/null 2>&1
sudo chown "$USERNAME":"$USERNAME" "$FILE"
echo backed up database to "$FILE"
