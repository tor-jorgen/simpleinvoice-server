#!/bin/bash

set -e

help() {
  echo "$0 <command>"
  echo
  echo "Perform operations against the S3 bucket."
  echo
  echo "<command>:  The command to perform"
  echo "  -cp, --copy <from> <to bucket name> [to]: Copy file to bucket"
  echo "    <from>: Relative path to file to copy from"
  echo "    <to bucket name>: Name of the bucket to copy to:"
  echo "      -  config:   the configuration bucket"
  echo "      -  invoices: the invoices bucket"
  echo "    [to]:   Path to copy to (optional). If not given, the file will be copied to the root of the config directory"
  echo "  -m, --mirror <from> <to bucket name>: Mirror files to bucket. This will mirror the source directory into the bucket. Updated files will be overwritten."
  echo "    <from>: Relative path to directory to copy from"
  echo "    <to bucket name>: Name of the bucket to copy to:"
  echo "      -  config:   the configuration bucket"
  echo "      -  invoices: the invoices bucket"
  echo
  echo "  -ls, --list <bucket name>: List the content of the bucket"
  echo "    <bucket name>: Name of the bucket to list:"
  echo "      -  config:   the configuration bucket"
  echo "      -  invoices: the invoices bucket"
  echo
  echo "  -h, --help: Show help"
}

if [ -z "$1" ]; then
  help
  exit 1
fi

if [ -f ".env" ]; then
    set -a
    source .env
    set +a
fi

if [ -z "$S3_SECRET_ACCESS_KEY" ]; then
  echo "ERROR: S3_SECRET_ACCESS_KEY is not set in your terminal or .env file."
  exit 1
fi

CONNECTION_PRE="${S3_CONNECTION_PRE:-http://localhost}"
PORT="${S3_PORT:-9000}"
URL="${CONNECTION_PRE}:${PORT}"
ACCESS_KEY="${S3_ACCESS_KEY_ID:-doc}"
SECRET_KEY="${S3_SECRET_ACCESS_KEY}"

while [[ "$1" == "--"* || "$1" == "-"* ]]; do
  case $1 in
    --copy|-cp)
      FROM_PATH="$2"
      TO_BUCKET="simple-invoice-$3"
      TO_PATH="$4"
      if [ -z "$FROM_PATH" ]; then
        help
        exit 1
      fi

      if [ ! -f "$FROM_PATH" ]; then
        echo "ERROR: File '$FROM_PATH' does not exist."
        exit 1
      fi

      if [ -z "$TO_BUCKET" ]; then
        echo "ERROR: To bucket must be given."
        exit 1
      fi

      if [ -z "$TO_PATH" ]; then
        TO_DIR=""
        TO_FILE_NAME=$(basename "$FROM_PATH")
      else
        TO_DIR=$(dirname "$TO_PATH")/
        TO_FILE_NAME=$(basename "$TO_PATH")
      fi
      CMD="mc mb --ignore-existing local/${TO_BUCKET} >/dev/null 2>&1 && mc cp /data/$FROM_PATH local/${TO_BUCKET}/${TO_DIR}${TO_FILE_NAME} >/dev/null 2>&1"
      MSG="'$FROM_PATH' has been uploaded to bucket '${TO_BUCKET}/${TO_DIR}${TO_FILE_NAME}'"
      echo "Uploading '$FROM_PATH' to bucket '${TO_BUCKET}/${TO_DIR}${TO_FILE_NAME}'"
      shift 1
      ;;
    --mirror|-m)
      FROM_PATH="$2"
      TO_BUCKET="simple-invoice-$3"
      if [ -z "$FROM_PATH" ]; then
        help
        exit 1
      fi

      if [ ! -d "$FROM_PATH" ]; then
        echo "ERROR: Directory '$FROM_PATH' does not exist."
        exit 1
      fi

      if [ -z "$TO_BUCKET" ]; then
        echo "ERROR: To bucket must be given."
        exit 1
      fi

      CMD="mc mb --ignore-existing local/${TO_BUCKET} >/dev/null 2>&1 && mc --debug mirror --overwrite /data/$FROM_PATH local/${TO_BUCKET}"
      echo "$CMD"
      MSG="'$FROM_PATH' has been uploaded to bucket '${TO_BUCKET}/${TO_DIR}${TO_FILE_NAME}'"
      echo "Uploading '$FROM_PATH' to bucket '${TO_BUCKET}/${TO_DIR}${TO_FILE_NAME}'"
      shift 1
      ;;
    --list|-ls)
      BUCKET_NAME="simple-invoice-$2"
      CMD="mc ls local/${BUCKET_NAME}/"
      MSG=""
      echo "Content of bucket '${BUCKET_NAME}':"
      shift 1
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

if docker run --rm \
  --env-file .env \
  -e S3_ACCESS_KEY_ID="${S3_ACCESS_KEY_ID:-doc}" \
  -v "$(pwd)":/data \
  --network host \
  --entrypoint /bin/sh \
  minio/mc \
  -c "mc alias set local ${URL} ${ACCESS_KEY} ${SECRET_KEY} --api s3v4 >/dev/null 2>&1 && ${CMD}"; then
  echo "$MSG"
else
  echo "Command failed!"
fi
