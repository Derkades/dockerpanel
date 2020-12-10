#!/bin/bash
export DOCKER_CLI_EXPERIMENTAL=enabled
docker buildx build -t derkades/dockerpanel --platform=linux/arm,linux/arm64,linux/amd64 . --push
