#!/usr/bin/env bash

# deploy.sh
# sterry1
# 2019-07-09

# This script should be run from the location where the project has been cloned.
# it requires some environment specific parameters found in the deployment_compose/etc directory
# config.properties - holds the connectivity information to backend Mongodb settings
# restheart.yml and security.yml work in conjunction for configuration of restheart server.
# keystores directory holds the AloeKeyStore.p12 used to validate the JWT and authenticate the user
#   this last file should be uploaded to the deployment host and should not be present in the
#   git source repository.
# The docker-compose command used to startup the server
#
#  $> docker-compose -f deployment_compose/restheart-compose.yml -p rsthrt up -d

