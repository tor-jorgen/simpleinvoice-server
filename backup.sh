#!/bin/bash

help() {
  echo "backup.sh [-f <file name>] [-d <directory>]"
  echo "Back up Simple Invoice data"
  echo "  -f <file prefix>: Name prefix of file to backup to. Name will be '<file name>-<timestamp>.zip'. Default is 'simpleinvoice-<timestamp>.zip"
  echo "  -d <directory>: Name of directory to backup to. Default is current directory"
  echo "Note! You need system administrator rights (sudo) to run this command, and you might have to enter your sudo password (depending on when you entered it the last time)."
  echo "Note! The configuration ('.env') is not backup up, since it contains secret values. This file must be copied manually to a safe place."
}

if [[ "$1" == "-h" || "$1" == "--help" ]]; then
  help
  exit 0
fi

if [[ ("$1" == "-f" && "$2" != "") ]]; then
  FILE="$2"
elif [[ ("$3" == "-f" && "$4" != "") ]]; then
  FILE="$4"
else
  FILE=simpleinvoice.zip
fi

if [[ ("$1" == "-d" && "$2" != "") ]]; then
  DIR="$2"
elif [[ ("$3" == "-d" && "$4" != "") ]]; then
  DIR="$4"
else
  DIR=.
fi

DB_DIR=$(docker volume inspect simple-invoice-db-data | sed -n 's/.*"Mountpoint": "\([^"]*\)".*/\1/p')
DOC_DIR=$(docker volume inspect simple-invoice-documents | sed -n 's/.*"Mountpoint": "\([^"]*\)".*/\1/p')
TIME=$(date --iso-8601=seconds)
FILENAME="${FILE/.zip/}-$TIME.zip"
sudo zip -r "$FILENAME" "$DB_DIR" "$DOC_DIR" ./.config >/dev/null 2>&1
sudo chown "$USERNAME":"$USERNAME" "$FILENAME"

if [ "$DIR" != "." ]; then
  sudo mv "$FILENAME" "$DIR"
fi

echo backed up data to "$DIR/$FILENAME"
