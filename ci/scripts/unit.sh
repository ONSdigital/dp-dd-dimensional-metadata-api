#!/bin/bash -eux

pushd dp-dd-dimensional-metadata-api
  mvn clean surefire:test
popd
