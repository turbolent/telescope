#!/bin/bash
set -euo pipefail

if [[ "$TRAVIS_PULL_REQUEST" == "false" && "$TRAVIS_BRANCH" == "master" ]]; then
  export PROJECT_NAME=$(basename "$TRAVIS_REPO_SLUG")

  echo "Building and pushing images ..."
  make push-images

  echo "Triggering deployment of API ..."
  wget --content-on-error --header "Authorization: $DEPLOYD_TOKEN" -qO- \
    https://$DEPLOYD_HOST/update\?name\=telescope-api\&image\=$(make api-image-reference)

  echo "Triggering deployment of Debugger ..."
  wget --content-on-error --header "Authorization: $DEPLOYD_TOKEN" -qO- \
    https://$DEPLOYD_HOST/update\?name\=telescope-debugger\&image\=$(make debugger-image-reference)

  echo "Triggering deployment of Interface ..."
  wget --content-on-error --header "Authorization: $DEPLOYD_TOKEN" -qO- \
    https://$DEPLOYD_HOST/update\?name\=telescope-interface\&image\=$(make interface-image-reference)
fi
