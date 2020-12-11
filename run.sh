#!/bin/bash
show_help() {
  echo "$1"

  echo -e "\nUSAGE
  ./run.sh [ACTION] [OPTION] [PROFILE]"

  echo -e "\nACTION
  build                         Build or rebuild services
  run                           Run services"

  echo -e "\nPROFILES
  test                          Run the microservice to implement changes using an in-memory database
  postgresql                    Run the microservice with a postgresql database connection"

  echo -e "\nOPTIONS
  -a, -A, --auth-service        Specify auht-service microservice
  -p, -P, --product-service     Specify product-service microservice
  -s, -S, --shopping-service    Specify shopping-service microservice"
}

validate_status(){
  HEALTHY=\"healthy\"
  UNHEALTHY=\"unhealthy\"
  cont_status=

  while  [[ $cont_status != $HEALTHY ]]; do
    cont_status=$(sudo docker inspect --format "{{json .State.Health.Status }}" $1)

    if [[ $cont_status == $UNHEALTHY ]]; then
      echo "The $2 container cannot be run."
      sudo docker stop $1
      sudo docker rm $1
      exit 1
    elif [[ $cont_status == $HEALTHY ]]; then
      echo "$2 container is ready."
    fi
  done
}

validate_profile(){
  if [[ ! -z $2 ]] && [[ $2 = $TEST || $2 = $POSTGRESQL ]] ; then
   eval $1=$2
  else
    if [[ -z $2 ]]; then
    show_help "Profiles are required!!";
    exit 1
    fi
    show_help "Invalid Profile \"$2\""
    exit 1
  fi
}

main(){
  if [[ $# -eq 0 ]]; then
    show_help "You need to specify the profile that each microservices will use"
    exit 1
  fi

  if [[ $1 == "build" || $1 == "run" ]]; then
    local action=$1
    shift
  else
    show_help "Invalid Action!!"; exit 1
  fi

  local arguments=$@

  local AUTH_P=""
  local PRODUCT_P=""
  local SHOPPING_P=""

  local OPS=":a:A:p:P:s:S:hH-:"
  while getopts $OPS options; do
    case $options in
      a|A) validate_profile AUTH_P $OPTARG
            echo "auth-service will use \"$AUTH_P\" profile"
            ;;
      p|P) validate_profile PRODUCT_P $OPTARG
            echo "product-service will use \"$PRODUCT_P\" profile"
            ;;
      s|S) validate_profile SHOPPING_P $OPTARG
            echo "shopping-service will use \"$SHOPPING_P\" profile"
            ;;
      h|H) show_help "Online Store."; exit 0
            ;;
      -) case $OPTARG in
          auth-service) validate_profile AUTH_P ${!OPTIND}
                        OPTIND=$(( $OPTIND + 1 ))
                        echo "auth-service will use \"$AUTH_P\" profile"
                        ;;
          product-service) validate_profile PRODUCT_P ${!OPTIND}
                        OPTIND=$(( $OPTIND + 1 ))
                        echo "product-service will use \"$PRODUCT_P\" profile"
                        ;;
          shopping-service) validate_profile SHOPPING_P ${!OPTIND}
                            OPTIND=$(( $OPTIND + 1 ))
                            echo "shopping-service will use \"$SHOPPING_P\" profile"
                            ;;
          help) show_help "Online Store."; exit 0
                ;;
          *) show_help "Invalid option \"-$OPTARG\""; exit 1;;
        esac;;
      :) show_help "Option \"-$OPTARG\" needs a profile"; exit 1;;
      *) show_help "Invalid option \"-$OPTARG\""; exit 1;;
    esac
  done

  if [[ $action == "build" ]]; then
    sudo docker-compose build --force-rm config-service
    sudo docker-compose build --force-rm registry-service
    sudo docker-compose build --force-rm --build-arg AUTH_PROFILE=$AUTH_P auth-service
    sudo docker-compose build --force-rm --build-arg PRODUCT_PROFILE=$PRODUCT_P product-service
    sudo docker-compose build --force-rm --build-arg SHOPPING_PROFILE=$SHOPPING_P shopping-service
    exit 0
  fi

  if [[ $arguments[@] =~ $POSTGRESQL ]]; then
    sudo docker-compose $action -d postgresql
    validate_status $(sudo docker ps -aqf "name=$POSTGRESQL") $POSTGRESQL
  fi

  sudo docker-compose $action -d config-service
  validate_status $(sudo docker ps -aqf "name=config-service") "config-service"

  sudo docker-compose $action -d registry-service
  validate_status $(sudo docker ps -aqf "name=registry-service") "registry-service"

  sudo docker-compose $action -d  -e AUTH_PROFILE=$AUTH_P auth-service
  sudo docker-compose $action -d  -e PRODUCT_PROFILE=$PRODUCT_P product-service
  sudo docker-compose $action -d  -e SHOPPING_PROFILE=$SHOPPING_P shopping-service

  exit 0
}

TEST="test"
POSTGRESQL="postgresql"
main $@
