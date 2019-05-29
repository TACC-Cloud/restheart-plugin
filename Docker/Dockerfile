#####################################################
#   Custom image build for RestHeart 3.10.0 that
#   includes the custom Tapis IDM plugin. Based on
#   Openjdk slim stretch (RH dist) image.
#
#
#   RHVERSION=3.10.0
#   docker  build -t aloe/restheart .
#   docker tag aloe/restheart "aloe/restheart:$RHVERSION"
#
#####################################################

FROM openjdk:8u212-jre-slim-stretch

LABEL maintainer="TACC <sterry1@tacc.utexas.edu>"

WORKDIR /opt/restheart
COPY etc/* /opt/restheart/etc/
COPY restheart-3.10.0.jar /opt/restheart/
COPY restheart-aloeplugin-1.0.jar /opt/restheart/
COPY lib/* /opt/restheart/lib/

CMD ["/bin/bash"]
EXPOSE 8080 4443
