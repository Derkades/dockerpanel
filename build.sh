#!/bin/bash
set -ex
mvn package shade:shade
docker build -t derkades/dockerpanel .
