#!/usr/bin/env bash

# Create TAG_VERSION - extract 'version=...' value from `gradle.properties` file and add 'v' prefix
TAG_VERSION="v$(cat gradle.properties | grep version= | cut -d'=' -f2)"

# Using the TAG_VERSION, check release doesn't exist before proceeding
GITHUB_RELEASE_URL="https://github.com/videofirst/vf-starter/releases/tag/$TAG_VERSION"
RELEASE_EXISTS=$(curl -LI -o /dev/null -w '%{http_code}\n' -s $GITHUB_RELEASE_URL)
if [[ "$RELEASE_EXISTS" == "200" ]]; then
  echo "Release [ $TAG_VERSION ] already exists at [ $GITHUB_RELEASE_URL ]"
  exit -1
fi

# Tag this release using GIT and push
echo "Creating GIT tag version [ $TAG_VERSION ]"
git tag $TAG_VERSION
git push origin --tags

echo "Finished"