#!/usr/bin/env bash
echo "Sharing $1 as folder $2"
docker stop updatesRepo
docker rm updatesRepo
docker run --name updatesRepo -p 8888:80 -v $1:/usr/share/nginx/html/$2:ro -d nginx
