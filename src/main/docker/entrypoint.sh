#!/bin/sh

echo "Launching"

JVM_INIT_MEM="${JVM_INIT_MEM:-1G}"
JVM_MAX_MEM="${JVM_MAX_MEM:-3G}"

echo "JVM initial mem: $JVM_INIT_MEM"
echo "JVM max mem: $JVM_MAX_MEM"

exec java -Xms${JVM_INIT_MEM} -Xmx${JVM_MAX_MEM} \
    -jar @project.artifactId@-@project.version@.@project.packaging@ \
    --spring.config.location=/app/config/application.yml
