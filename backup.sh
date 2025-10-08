#!/bin/bash

help() {
  echo "backup.sh [file name]"
  echo "Back up Simple Invoice data"
  echo "  file name: Name of file to backup to. Default is 'simpleinvoice-<timestamp>.zip'"
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

DB_DIR=$(docker volume inspect simple-invoice-db-data | sed -n 's/.*"Mountpoint": "\([^"]*\)".*/\1/p')
DOC_DIR=$(docker volume inspect simple-invoice-documents | sed -n 's/.*"Mountpoint": "\([^"]*\)".*/\1/p')
TIME=$(date --iso-8601=seconds)
FILENAME="${FILE/.zip/}-$TIME.zip"
sudo zip -r "$FILENAME" "$DB_DIR" "$DOC_DIR" ./.config .env >/dev/null 2>&1
sudo chown "$USERNAME":"$USERNAME" "$FILENAME"
echo backed up data to "$FILENAME"
