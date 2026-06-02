#!/bin/bash

IMAGE=simpleinvoice-server-trivy:latest

docker build -t "$IMAGE" --no-cache .

docker run \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v .:/mount -w /mount  \
  -v trivy-cache:/root/.cache/ \
  --entrypoint sh aquasec/trivy \
  -c "trivy --severity HIGH,CRITICAL image $IMAGE && \
    trivy --severity HIGH,CRITICAL config ."
