#!/bin/sh

echo "Launching"

exec java -jar @project.artifactId@-@project.version@.@project.packaging@ \
          --spring.config.location=/app/config/application.yml
