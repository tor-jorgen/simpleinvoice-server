#!/bin/bash

help() {
  echo "restore.sh [OPTIONS] FILE"
  echo
  echo "Restore Simple Invoice data from FILE (a 'tar.gz' file created by 'backup.sh')"
  echo
  echo "OPTIONS:"
  echo " -h, --help: Show help"
  echo
  echo "Note! Stop the application before running the backup by executing './stop.sh'"
  echo "Note! This script might not work if the backup was created on a different computer."
}

if [[ "$1" == "-h" || "$1" == "--help" ]]; then
  help
  exit 0
fi

if ! docker volume inspect simple-invoice-db-data >/dev/null 2>&1 || ! docker volume inspect simple-invoice-s3-data >/dev/null 2>&1; then
  echo "Missing volumes. You must run the application at least once before restoring data!"
  exit 1
fi

if [[ -n "$1" ]]; then
  BACKUP_FILE="$1"
else
  echo "FILE must be specified."
  echo
  help
  exit 0
fi

echo
echo "NOTE! This will restore data from $BACKUP_FILE and replace/add the following data:"
echo "- Database:    Replace database"
echo "- Other files: Replace existing/add new/keep files not in the backup"
echo
echo "Also ensure that the application has been stopped (by running './stop.sh')!"
echo
echo "Are you sure you want to continue? (Y/n)"
read -r confirmation
if [[ ! "$confirmation" =~ ^[Yy]$ ]]; then
  echo "Restore cancelled."
  exit 0
fi

RESTORE_DIR=$(dirname "$BACKUP_FILE")
RESTORE_FILE=$(basename "$BACKUP_FILE")

# Restore database into 'db-data' volume and S3 data into ''s3-data' volume
# Delete the databse files under
echo "Restoring data from $BACKUP_FILE"
(docker run \
  --rm \
  --entrypoint /bin/sh \
  -v simple-invoice-db-data:/db-data \
  -v simple-invoice-s3-data:/s3-data \
  -v "$RESTORE_DIR":/input \
  alpine \
  -c "
  find /db-data -mindepth 1 -delete && \
  tar -xz -f /input/$RESTORE_FILE -C /db-data --strip-components=1 db-data/. && \
  tar -xz -f /input/$RESTORE_FILE -C /s3-data --strip-components=1 s3-data/.") || ERROR=1

if [[ -n "$ERROR" ]]; then
  echo 'Aborted! An error occurred while restoring.'
  exit 1
fi

echo "Finished restoring data"
