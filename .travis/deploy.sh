#!/bin/bash
set -euo pipefail

if [[ "$TRAVIS_PULL_REQUEST" == "false" && "$TRAVIS_BRANCH" == "master" ]]; then
  export PROJECT_NAME=$(basename "$TRAVIS_REPO_SLUG")

  echo "Building and pushing images ..."
  make push-images

# TODO:
#   echo "Triggering deploy ..."
#   wget --content-on-error --header "Authorization: $TOKEN" -qO- \
#     http://$DEPLOYD_HOST/update\?service\=$DEPLOYD_SERVICE\&image\=$DEPLOYD_IMAGE:latest
fi
