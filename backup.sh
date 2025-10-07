#!/bin/bash

help() {
  echo "sudo backup.sh [file name]"
  echo "Back up Simple Invoice database"
  echo "  file name: Name of file to backup to. Default is 'simpleinvoice.zip'"
  echo "Note that you must run this command with sudo, otherwise you cannot access the database files"
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
zip -r "$FILE" "$DIR" >/dev/null 2>&1
chown "$USERNAME":"$USERNAME" "$FILE"
echo backed up database to "$FILE"
