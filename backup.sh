#!/bin/bash

help() {
  echo "backup.sh [OPTIONS]"
  echo
  echo "Backup Simple Invoice data into a tar.gz file"
  echo
  echo "OPTIONS:"
  echo " -d, --directory DIRECTORY: Directory to save the backup to (default: current directory)"
  echo " -f, --file FILE:           Base name for the file the backup will be stored to (default: simpleinvoice)"
  echo " -h, --help:                Show help"
  echo
  echo "Note! Stop the application before running the backup by executing './stop.sh'"
  echo "Note! The configuration file ('.env') is not backup up, since it contains secret values. This file must be copied manually to a safe place."
}

FILE="simpleinvoice"
OUTPUT_PATH="."

while [[ "$1" == "--"* || "$1" == "-"* ]]; do
  case $1 in
    --directory|-d)
      OUTPUT_PATH="$2"
      shift 2
      ;;
    --file|-f)
      FILE="$2"
      shift 2
      ;;
    --help|-h)
      help
      exit 0
      ;;
    *)
      echo "Unknown option: $1"
      echo
      help
      exit 1
      ;;
  esac
done

if ! docker volume inspect simple-invoice-db-data >/dev/null 2>&1 || ! docker volume inspect simple-invoice-s3-data >/dev/null 2>&1; then
  echo "Missing volumes. You must run the application at least once before backing up data!"
  exit 1
fi

USER_ID=$(id -u)
GROUP_ID=$(id -g)
TIME=$(date --iso-8601=seconds)
FILENAME="${FILE/.tar/}-$TIME.tar"

if [ ! -d "$OUTPUT_PATH" ]; then
  echo "Directory '$OUTPUT_PATH' does not exist"
  exit 1
fi

echo
echo "This will back up your data to $OUTPUT_PATH/$FILENAME.gz"
echo
echo "Ensure that the application has been stopped (by running './stop.sh')!"
echo
echo "Are you sure you want to continue? (Y/n)"
read -r confirmation
if [[ ! "$confirmation" =~ ^[Yy]$ ]]; then
  echo "Backup cancelled."
  exit 0
fi

echo "Backing up data"

# Back up database into 'db-data/' directory and S3 data into 's3-data/' directory
(docker run \
  --rm \
  --entrypoint /bin/sh \
  -v simple-invoice-db-data:/db-data \
  -v simple-invoice-s3-data:/s3-data \
  -v "$OUTPUT_PATH:/output" \
  alpine \
  -c "apk add --no-cache tar >/dev/null 2>&1 && \
  tar -cf /output/$FILENAME -C /db-data  --transform 's|^|db-data/|' . && \
  tar -rf /output/$FILENAME -C /s3-data  --transform 's|^|s3-data/|' . && \
  gzip /output/$FILENAME && \
  chown $USER_ID:$GROUP_ID /output/$FILENAME.gz") || ERROR=1

if [[ -n "$ERROR" ]]; then
  echo 'Aborted! An error occurred while backing up.'
  rm -f "$OUTPUT_PATH/$FILENAME.gz" >/dev/null 2>&1
  exit 1
fi

echo "Backed up data to: $OUTPUT_PATH/$FILENAME.gz"
