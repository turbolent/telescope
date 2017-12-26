#!/bin/bash
set -euo pipefail

if [[ "$TRAVIS_PULL_REQUEST" == "false" && "$TRAVIS_BRANCH" == "master" ]]; then
  export PROJECT_NAME=$(basename "$TRAVIS_REPO_SLUG")

  echo "Building and pushing images ..."
  make push-images

  echo "Triggering deployment of API ..."
  wget --content-on-error --header "Authorization: $DEPLOYD_TOKEN" -qO- \
    http://$DEPLOYD_HOST/update\?service\=$DEPLOYD_SERVICE\&image\=$(make api-image-reference)
fi
