#!/bin/bash
set -ex
docker run --rm --privileged docker/binfmt:66f9012c56a8316f9244ffd7622d7c21c1f6f28d
export DOCKER_CLI_EXPERIMENTAL=enabled
set +e
docker buildx rm dockerpanel_builder
set -e
docker buildx create --use --name dockerpanel_builder
docker run --rm -u "$UID" -v "$PWD:/data" -v "$HOME/.m2:/var/maven/.m2" -e MAVEN_CONFIG=/var/maven/.m2 -w /data maven:3-adoptopenjdk-14 mvn -Duser.home=/var/maven clean package shade:shade
docker buildx build -t derkades/dockerpanel --platform=linux/arm,linux/arm64,linux/amd64 . --push
docker buildx rm dockerpanel_builder
