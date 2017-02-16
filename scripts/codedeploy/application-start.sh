#!/bin/bash

AWS_REGION=
CONFIG_BUCKET=
ECR_REPOSITORY_URI=
GIT_COMMIT=

INSTANCE=$(curl -s http://instance-data/latest/meta-data/instance-datasetId)
CONFIG=$(aws --region $AWS_REGION ec2 describe-tags --filters "Name=resource-datasetId,Values=$INSTANCE" "Name=key,Values=Configuration" --output text | awk '{print $5}')

if [[ $DEPLOYMENT_GROUP_NAME =~ [a-z]+-publishing ]]; then
  CONFIG_DIRECTORY=publishing
else
  CONFIG_DIRECTORY=web
fi

(aws s3 cp s3://$CONFIG_BUCKET/dp-dd-dimensional-metadata-api/$CONFIG_DIRECTORY/$CONFIG.asc . && gpg --decrypt $CONFIG.asc > $CONFIG) || exit $?

source $CONFIG && docker run -d         \
  --env=BASE_URL=$BASE_URL              \
  --env=DB_DRIVER=$DB_DRIVER            \
  --env=DB_PASSWORD=$DB_PASSWORD        \
  --env=DB_URL=$DB_URL                  \
  --env=DB_USER=$DB_USER                \
  --env=SERVER_PORT=$SERVER_PORT        \
  --name=dp-dd-dimensional-metadata-api \
  --net=$DOCKER_NETWORK                 \
  --restart=always                      \
  $ECR_REPOSITORY_URI/dp-dd-dimensional-metadata-api:$GIT_COMMIT
