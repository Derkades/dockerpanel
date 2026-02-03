#!/bin/bash
! podman manifest exists dockerpanel || podman manifest rm dockerpanel
podman manifest create dockerpanel
podman build --platform=linux/arm64,linux/amd64 --manifest dockerpanel .
podman manifest push --all dockerpanel docker://docker.io/derkades/dockerpanel
