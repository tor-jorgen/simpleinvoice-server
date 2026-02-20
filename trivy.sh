#!/bin/bash

IMAGE=simpleinvoice-server-trivy:latest

docker build -t $IMAGE --no-cache .

# Scan image
trivy image --severity HIGH,CRITICAL $IMAGE

# Check Dockerfile
trivy --severity HIGH,CRITICAL config .
