#!/bin/bash
validate_status(){
  timeout -v $1 ./healthcheck.bash $(sudo docker ps -aqf "name=$2") "$2"

  if [[ $? -gt 0 ]]; then
    echo "The $2 container cannot be up."
    sudo docker stop $2
    sudo docker rm $2
    exit 62
  fi
}

main(){
    sudo docker-compose up -d postgresql
    validate_status 20s "postgresql"

    sudo docker-compose up -d config-service
    validate_status 30s "config-service"

    sudo docker-compose up -d registry-service
    validate_status 30s "registry-service"

    sudo docker-compose up -d auth-service
    sudo docker-compose up -d product-service
    sudo docker-compose up -d shopping-service
exit 0
}

main
