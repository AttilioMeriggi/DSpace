#/bin/bash
mvn install:install-file \
   -Dfile=jetty-all-9.4.19.v20190610-uber.jar \
   -DgroupId=org.eclipse.jetty.aggregate \
   -DartifactId=jetty-all \
   -Dversion=9.4.19.v20190610 \
   -Dpackaging=jar \
   -DpomFile=jetty-all-9.4.19.v20190610.pom
