#!/bin/bash

echo "--------------- 서버 배포 시작 -----------------"
docker stop threed || true
docker rm threed || true
docker pull 113790821400.dkr.ecr.ap-northeast-2.amazonaws.com/threed:latest
docker run -d --name threed -p 80:8080 113790821400.dkr.ecr.ap-northeast-2.amazonaws.com/threed:latest
echo "--------------- 서버 배포 끝 -----------------"
