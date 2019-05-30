#!/bin/bash
set -e
# =================================================================
# Script to build the image for RestHeart deployment
# includes the uber jar from RestHeart server build
#   restheart-3.10.0.jar
# this jar includes all the dependencies needed for standard
# RestHeart server instance.
# Additional dependencies are found for the Authentication plugin
# in the Docker/lib directory.
# The image is created and published to the private registry for
# deployment.
# =================================================================

echo "###### Building Docker image..."
RHVERSION=3.10.0

cd Docker

echo "###### Building Docker image for RESTHeart Version $RHVERSION"
docker  build -t aloe/restheart:$RHVERSION .

docker tag aloe/restheart:$RHVERSION "jenkins2.tacc.utexas.edu:5000/aloe/restheart:$RHVERSION"
echo "###### Docker image successfully built."

docker push "jenkins2.tacc.utexas.edu:5000/aloe/restheart:$RHVERSION"