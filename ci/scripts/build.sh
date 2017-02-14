#!/bin/bash -eux

pushd dp-dd-dimensional-metadata-api
  mvn -U clean package -DskipTests=true
popd

cp -r dp-dd-dimensional-metadata-api/target/* target/
