#!/bin/bash
set -ex
docker run --rm -u "$UID" -v "$PWD:/data" -v "$HOME/.m2:/var/maven/.m2" -e MAVEN_CONFIG=/var/maven/.m2 -w /data maven:3-adoptopenjdk-14 mvn -Duser.home=/var/maven clean package shade:shade
export DOCKER_CLI_EXPERIMENTAL=enabled
docker buildx build -t derkades/dockerpanel --platform=linux/arm,linux/arm64,linux/amd64 . --push
