#!/bin/bash
#
# It seems there is no way to log in using the sbt-native-packager,
# so this helper script has to do the login before sbt docker:publish
#

docker login --username="${DEPLOY_USERNAME}" --password="${DEPLOY_PASSWORD}" --email="timo.schmid@gmail.com" docker.timo-schmid.ch

