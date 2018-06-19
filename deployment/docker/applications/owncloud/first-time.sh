#!/usr/bin/env bash
#docker volume rm OCfiles OCdb OCbackup OCredis
echo "[i] creating volumes for OC..."
docker volume create OCfiles
echo "[i] command returned $?"
docker volume create OCdb
echo "[i] command returned $?"
docker volume create OCbackup
echo "[i] command returned $?"
docker volume create OCredis
echo "[i] command returned $?"
echo "[i] volumes created: "
docker volume ls |grep "OC"
echo "[i] command returned $?"

