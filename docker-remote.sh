#!/bin/sh

echo "-----------------------------------------------------"
echo "VF STARTER - BUILD / REMOTE DEPLOY"
echo "-----------------------------------------------------"
echo

function message () {
  echo
  echo "----------------- $1 -----------------"
  echo
}

# This script runs (1) a gradle build then (2) build local docker image.  This script is designed for remote deployment.
# NOTE, this script expects a VF_STARTER_DOCKER_REPO variable to be set, otherwise it will fail.

if [[ -z "${VF_STARTER_DOCKER_REPO}" ]]; then
  echo "Environment variable [ VF_STARTER_DOCKER_REPO ] not available - please set and try again e.g."
  echo
  echo "   export VF_STARTER_DOCKER_REPO=12345678.dkr.ecr.eu-west-1.amazonaws.com/vf-starter"
  echo
  exit -1
else
  echo "Tagging and deploying build with tag [ $VF_STARTER_DOCKER_REPO ]"
  echo
fi

set -e

message "Logging into repo"
# NOTE, this assume AWS CLI V1 is install. Update depending on your Docker REPO.
$(aws ecr get-login --no-include-email --region eu-west-1)

message "Performing Gradle build"
./gradlew clean build

message "Tagging Docker image"
docker build . -t $VF_STARTER_DOCKER_REPO

message "Pushing docker image"
docker push $VF_STARTER_DOCKER_REPO

message "Docker image uploaded!"
