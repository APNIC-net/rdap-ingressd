#!/bin/sh

echo "Launching with arguments: $@"
set -e
JAVA_OPTS="${JAVA_OPTS}"
JVM_INIT_MEM="${JVM_INIT_MEM:-128M}"
JVM_MAX_MEM="${JVM_MAX_MEM:-256M}"
echo "Starting application"
exec java -jar ${JAVA_OPTS} @project.artifactId@-@project.version@.@project.packaging@ "$@"
