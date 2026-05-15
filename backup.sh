#!/bin/bash

help() {
  echo "backup.sh [OPTIONS]"
  echo
  echo "Backup Simple Invoice data into a tar.gz file"
  echo
  echo "Options:"
  echo " -d, --directory: Directory to save backup to (default: current directory)"
  echo " -f, --file:      Base name for backup file (default: simpleinvoice)"
  echo " -h, --help:      Show help"
  echo
  echo "Note! The configuration ('.env') is not backup up, since it contains secret values. This file must be copied manually to a safe place."
  echo "Note! The script will download a Docker image the first time it is run, and will do the backup through the image (this avoids the need for a root user)."
}

get_config_path() {
  CFG_PATH=$(grep "^LOCAL_CONFIG_DIRECTORY=" ".env" | cut -d '=' -f 2)
  if [ "$CFG_PATH" == "" ]; then
    CFG_PATH=".config"
  fi

  if [[ ! "$CFG_PATH" =~ ^/ ]]; then
    CFG_PATH="$(pwd)/$CFG_PATH"
  fi
}

get_output_path() {
  if [ ! -d "$OUTPUT_PATH" ]; then
    echo "Directory '$OUTPUT_PATH' does not exist"
    exit 1
  fi
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

USER_ID=$(id -u)
GROUP_ID=$(id -g)
DB_PATH=$(docker volume inspect simple-invoice-db-data | sed -n 's/.*"Mountpoint": "\([^"]*\)".*/\1/p')
DOC_PATH=$(docker volume inspect simple-invoice-documents | sed -n 's/.*"Mountpoint": "\([^"]*\)".*/\1/p')
TIME=$(date --iso-8601=seconds)
FILENAME="${FILE/.tar/}-$TIME.tar"

get_config_path
get_output_path

echo "Backing up data"

docker run \
--rm \
--entrypoint /bin/sh \
-v "$OUTPUT_PATH:/output" \
-v "$DB_PATH:/db" \
-v "$DOC_PATH:/doc" \
-v "$CFG_PATH:/cfg" \
alpine \
-c "apk add --no-cache tar >/dev/null 2>&1 && \
cd /db && \
tar -cf /output/$FILENAME --transform 's|^|db/|' . && \
cd /doc && \
tar -rf /output/$FILENAME --transform 's|^|doc/|' . && \
cd /cfg && \
tar -rf /output/$FILENAME --transform 's|^|cfg/|' . && \
gzip /output/$FILENAME && \
chown $USER_ID:$GROUP_ID /output/$FILENAME.gz"

echo "Backed up data to: $OUTPUT_PATH/$FILENAME.gz"
