#!/bin/bash

# Script to compile and run dockerpanel locally, for testing

set -ex
docker run --rm -u "$UID" -v "$PWD:/data" -v "$HOME/.m2:/var/maven/.m2" -e MAVEN_CONFIG=/var/maven/.m2 -w /data maven:3-adoptopenjdk-14 mvn -Duser.home=/var/maven clean package shade:shade
docker build -t derkades/dockerpanel .
docker run --rm -v '/var/run/docker.sock:/var/run/docker.sock' -p '8080:8080' -e PORT=8080 derkades/dockerpanel
