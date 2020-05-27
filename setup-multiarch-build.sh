#!/bin/bash
# Only tested on ubuntu, use at your own risk.

docker run --rm --privileged docker/binfmt:66f9012c56a8316f9244ffd7622d7c21c1f6f28d
export DOCKER_CLI_EXPERIMENTAL=enabled
docker buildx rm mybuilder
docker buildx create --use --name mybuilder
