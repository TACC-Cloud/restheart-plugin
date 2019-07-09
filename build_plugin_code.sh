#!/usr/bin/env bash

# build_plugin_code.sh
# sterry1
# 2019-05-17

# basic build and install for plugin artifacts
mvn -f pom.xml clean install

# stage artifacts to Docker directory for building a new image
cp -rf target/restheart-aloeplugin-1.0.jar Docker/