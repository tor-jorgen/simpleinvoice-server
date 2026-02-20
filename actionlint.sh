#!/bin/bash

# Lint the .github files

docker run --rm -v .:/server -w /server node:22-slim sh -c "npm install node-actionlint && npx node-actionlint"
