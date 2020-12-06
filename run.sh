#!/bin/bash
#set -u

show_help() {
  echo "$1"

  echo -e "\nUSAGE
  ./run.sh [OPTION] [PROFILE]"

  echo -e "\nPROFILES
  test          Run the microservice to implement changes using an in-memory database
  postgresql    Run the microservice with a postgresql database connection"

  echo -e "\nOPTIONS
  -a, -A    Specify auht-service microservice
  -p, -P    Specify product-service microservice
  -s, -S    Specify shopping-service microservice"
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

  arguments=$@
  local AUTH_P=""
  local PRODUCT_P=""
  local SHOPPING_P=""

  local OPS=":a:A:p:P:s:S:-:"

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
          *) show_help "Invalid option \"-$OPTARG\""; exit 1;;
        esac;;
      :) show_help "Option \"-$OPTARG\" needs a profile"; exit 1;;
      *) show_help "Invalid option \"-$OPTARG\""; exit 1;;
    esac
  done

    if [[ $arguments[@] =~ $POSTGRESQL ]]; then
      #echo "postgresql"
      sudo docker-compose run -d postgresql
    fi
    #echo "config"
    sudo docker-compose run -d config-service
    #echo "registry"
    sudo docker-compose run -d registry-service
    #echo "auth $AUTH_P"
    sudo docker-compose run -d  -e AUTH_PROFILE=$AUTH_P auth-service
    #echo "product $PRODUCT_P"
    sudo docker-compose run -d  -e PRODUCT_PROFILE=$PRODUCT_P product-service
    #echo "shopping $SHOPPING_P"
    sudo docker-compose run -d  -e SHOPPING_PROFILE=$SHOPPING_P shopping-service
exit 0
}

TEST="test"
POSTGRESQL="postgresql"
main $@
