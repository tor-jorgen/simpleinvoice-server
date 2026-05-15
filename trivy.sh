#!/bin/bash

IMAGE=simpleinvoice-server-trivy:latest

docker build -t $IMAGE --no-cache .

#trivy clean -a

# Scan image
trivy image --severity HIGH,CRITICAL $IMAGE

# Check Dockerfile
trivy --severity HIGH,CRITICAL config .

#trivy image -f json simpleinvoice-server-trivy:latest | grep -C 5 "3.0.3"
