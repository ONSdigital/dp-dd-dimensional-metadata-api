#!/bin/bash

if [[ $(docker inspect --format="{{ .State.Running }}" dp-dd-dimensional-metadata-api) == "false" ]]; then
  exit 1;
fi
