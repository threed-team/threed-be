#!/bin/bash

echo "--------------- 서버 배포 시작 -----------------"
docker stop threed || true
docker rm threed || true
docker pull 113790821400.dkr.ecr.us-east-1.amazonaws.com/threed:latest
docker run -d --name threed -p 8080:8080 113790821400.dkr.ecr.us-east-1.amazonaws.com/threed:latest
echo "--------------- 서버 배포 끝 -----------------"
