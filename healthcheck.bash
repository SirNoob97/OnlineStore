#!/bin/bash
healthy=\"healthy\"
unhealthy=\"unhealthy\"
container_status=
while  [[ $container_status != $healthy ]]; do
  container_status=$(sudo docker inspect --format "{{json .State.Health.Status }}" $1)

  if [[ $container_status == $unhealthy ]]; then
    exit 1
  elif [[ $container_status == $healthy ]]; then
    echo "$2 container is ready."
    exit 0
  fi
done
