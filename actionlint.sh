#!/bin/bash

# Lint the .github files

docker run --rm \
  -v .:/mount -w /mount \
  node:22-slim sh \
  -c "npm install -g node-actionlint && npx node-actionlint"
