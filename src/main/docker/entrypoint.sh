#!/bin/sh

echo "Launching"

exec java -jar @project.artifactId@-@project.version@.@project.packaging@
