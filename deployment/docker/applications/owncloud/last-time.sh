#!/usr/bin/env bash
echo "[i] deleting volumes for OC..."
docker volume rm OCfiles OCdb OCbackup OCredis
echo "[i] command returned $?"

