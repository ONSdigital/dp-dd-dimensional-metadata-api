#!/bin/bash -eux

pushd dp-dd-dimensional-metadata-api
  mvn clean package dependency:copy-dependencies -DskipTests=true
popd

cp -r dp-dd-dimensional-metadata-api/target/* target/
