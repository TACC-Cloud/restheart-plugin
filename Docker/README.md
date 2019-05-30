This directory holds the context for a Docker build the image for RestHeart 
server deployment. It includes the uber jar from RestHeart server build **restheart-3.10.0.jar**

This jar includes all the dependencies needed for standard RestHeart server instance.
with additional dependencies found for the Authentication plugin in the Docker/lib directory.

The build-docker.sh script, found in the root of the project, will build and publish the image to 
the private registry for deployment.
