This project holds the source code, the libraries and Docker files
used to build the restheart-aloeplugin artifacts, create the image
for the RestHeart server integrated with Aloe/Tapis JWT validation.

1. build_plugin_code.sh : A build script, using maven, does the initial build of the restheart-aloeplugin-xx.jar
2. build-docker.sh : Takes the artifact jar from the plugin build and creates a Docker image
3. deploy.sh : 