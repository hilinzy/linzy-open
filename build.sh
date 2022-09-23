#!/bin/bash

echo "build ..."
if [ "local_plugin" = $1 ] ; then
  mvn clean install -pl linzy-open-common,linzy-open-mongo -am
elif [ "publish_plugin" = $1 ] ; then
  mvn clean deploy -pl linzy-open-common,linzy-open-mongo -am
fi
echo "build done."