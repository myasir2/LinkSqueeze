#!/usr/bin/env bash

# Build the project
./gradlew clean && ./gradlew build

if [[ $? -eq 0 ]]; then
  tag=latest #$(openssl rand -hex 6)

  # Build image and tag it
  docker build --platform linux/amd64,linux/arm64 -t myasir/link-squeeze .
  docker tag myasir/link-squeeze:latest public.ecr.aws/q8s2a7f3/myasir/link-squeeze:latest

  # Get ECR login creds, and deploy
  aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin public.ecr.aws/q8s2a7f3
  docker push public.ecr.aws/q8s2a7f3/myasir/link-squeeze:latest
else
  echo "Gradle build failed"
fi
