#!/bin/bash
validate_status(){
  HEALTHY=\"healthy\"
  UNHEALTHY=\"unhealthy\"
  cont_status=

  while  [[ $cont_status != $HEALTHY ]]; do
    cont_status=$(sudo docker inspect --format "{{json .State.Health.Status }}" $1)

    if [[ $cont_status == $UNHEALTHY ]]; then
      echo "The $2 container cannot be up."
      sudo docker stop $1
      sudo docker rm $1
      exit 1
    elif [[ $cont_status == $HEALTHY ]]; then
      echo "$2 container is ready."
    fi
  done
}

main(){
    sudo docker-compose up -d postgresql
    validate_status $(sudo docker ps -aqf "name=postgresql") "postgresql"

    sudo docker-compose up -d config-service
    validate_status $(sudo docker ps -aqf "name=config-service") "config-service"

    sudo docker-compose up -d registry-service
    validate_status $(sudo docker ps -aqf "name=registry-service") "registry-service"

    sudo docker-compose up -d auth-service
    sudo docker-compose up -d product-service
    sudo docker-compose up -d shopping-service
exit 0
}

main
