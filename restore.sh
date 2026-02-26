#!/bin/bash

help() {
  echo "restore.sh [OPTIONS] FILE"
  echo
  echo "Restore Simple Invoice data from a tar.gz FILE created by backup.sh"
  echo
  echo "Options:"
  echo " -h, --help: Show help"
  echo
  echo "Note! This script might not work if the backup was created on a different computer."
  echo "Note! The configuration ('.env') is not restored, since it was not backed up. This file must be added manually, if it is not there already."
  echo "Note! The script will download a Docker image the first time it is run, and will do the restore through the image (this avoids the need for a root user)."
}

get_config_dir() {
  CFG_PATH=$(grep "^LOCAL_CONFIG_DIRECTORY=" ".env" | cut -d '=' -f 2)
  if [ "$CFG_PATH" == "" ]; then
    CFG_PATH=".config"
  fi

  CFG_DIR="$CFG_PATH"

  if [[ ! "$CFG_PATH" =~ ^/ ]]; then
    CFG_PATH="$(pwd)/$CFG_PATH"
  fi
}

if [[ "$1" == "-h" || "$1" == "--help" ]]; then
  help
  exit 0
fi

if [[ -n "$1" ]]; then
  BACKUP_FILE="$1"
else
  echo "FILE must be specified."
  echo
  help
  exit 0
fi

if ! docker volume inspect simple-invoice-db-data >/dev/null 2>&1 || docker volume inspect simple-invoice-dbocuments >/dev/null 2>&1; then
  echo "Missing volumes. You must run the application at least once before restoring data!"
  exit 1
fi

DB_PATH=$(docker volume inspect simple-invoice-db-data | sed -n 's/.*"Mountpoint": "\([^"]*\)".*/\1/p') >/dev/null 2>&1
DOC_PATH=$(docker volume inspect simple-invoice-documents | sed -n 's/.*"Mountpoint": "\([^"]*\)".*/\1/p') >/dev/null 2>&1

get_config_dir

echo
echo "NOTE! This will restore data from $BACKUP_FILE and replace/overwrite/add data stored in the following locations:"
echo "- '$DB_PATH' (replace)"
echo "- '$DOC_PATH' (overwrite/add)"
echo "- '$CFG_PATH' (overwrite/add)"
echo
echo "Also ensure that the application has been stopped!"
echo
echo "Are you sure you want to continue? (Y/n)"
read -r confirmation
if [[ ! "$confirmation" =~ ^[Yy]$ ]]; then
  echo "Restore cancelled."
  exit 0
fi

mkdir -p "$CFG_PATH"

RESTORE_DIR=$(dirname "$BACKUP_FILE")
RESTORE_FILE=$(basename "$BACKUP_FILE")

echo "Restoring data from $BACKUP_FILE"
(docker run \
--rm \
--entrypoint /bin/sh \
-v "$RESTORE_DIR":/input \
-v "$DB_PATH":/db \
-v "$DOC_PATH":/doc \
-v "$CFG_DIR":/cfg \
-v "/":/root \
alpine \
-c "
find /db -mindepth 1 -delete && \
tar -xz -f /input/$RESTORE_FILE -C /db --strip-components=1 db/. && \
tar -xz -f /input/$RESTORE_FILE -C /doc --strip-components=1 doc/. && \
tar -xz -f /input/$RESTORE_FILE -C /cfg --strip-components=1 cfg/.") || ERROR=1

if [[ -n "$ERROR" ]]; then
  echo 'Aborted! This is maybe not a valid backup file, or some other error happened!'
  exit 1
fi

echo "Finished restoring data"
