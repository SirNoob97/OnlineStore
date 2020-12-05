#!/bin/bash

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

if [[ $# -eq 0 ]]; then
  show_help "You need to specify the profile that each microservices will use"
  exit 1
fi

read -p "Will you use the postgres container?
If you don't want to use postgresql microservices will run with h2 database [y/n]" user_input

if [[ "y" == ${user_input,,} ]]; then
  AUTH_P=""
  PRODUCT_P=""
  SHOPPING_P=""

  arguments=`getopt -o "a:A:p:P:s:S:" -l "auth-service:,product-service:,shopping-service:" -n microservices -- "$@"`
  echo $arguments
  eval set -- "$arguments"
  while :
  do
    case $1 in
      -a|-A) AUTH_P=$2; echo "a getopts $AUTH_P"; shift 2;;
      -p|-P) PRODUCT_P=$2; echo "p getopts $PRODUCT_P"; shift 2;;
      -s|-S) SHOPPING_P=$2; echo "s getopts $SHOPPING_P"; shift 2;;
      :) show_help "Syntax ERROR; option "\"-$2\"" requires an profile"; exit 1;;
      --) shift; break ;;
      *) show_help "Invalid option "\"-$2\"""; exit 1;;
    esac
  done
  #while [ $# -gt 0 ]; do
    #if [[ $1 =~ ^((-{1})([p|P]$|[a|A]$|[s|S]$)) ]]; then
      #opt=$1; shift;
      #current_arg=${1,,}

      #if [[ $current_arg =~ ^-{1,2}.* ]]; then
        #show_help "You may have left an argument blank. Double check your command"; exit 0
      #fi

      #if [[ $current_arg == "test" || $current_arg == "postgresql" ]] ; then

        #case $opt in
          #"-a"|"-A") AUTH_P=$current_arg; echo $AUTH_P; shift;;
          #"-p"|"-P") PRODUCT_P=$current_arg; echo $PRODUCT_P; shift;;
          #"-s"|"-S") SHOPPING_P=$current_arg; echo $SHOPPING_P; shift;;
          #*) echo "Invalid option \""$opt"\"" >&2
          #exit 1;;
        #esac

      #else
        #show_help "Invalid Profile $current_arg"; exit 0
      #fi

    #else
      #show_help "Invalid option \"$1\""; exit 0
      #exit 1
    #fi
  #done

  if [[ $PRODUCT_P == "" || $AUTH_P == "" || $SHOPPING_P == "" ]]; then
    show_help "Profiles are required!!"; exit 0
    exit 1
  else
    echo "postgresql"
    #sudo docker-compose run -d postgresql
    echo "config"
    #sudo docker-compose run -d config-service
    echo "registry"
    #sudo docker-compose run -d registry-service
    echo "auth"
    #sudo docker-compose run -d  -e AUTH_PROFILE=$AUTH_P auth-service
    echo "product"
    #sudo docker-compose run -d  -e PRODUCT_PROFILE=$PRODUCT_P product-service
    echo "shopping"
    #sudo docker-compose run -d  -e SHOPPING_PROFILE=$SHOPPING_P shopping-service
  fi
else
  if [[ "n" == ${user_input,,} ]]; then
    echo "no postgres"
    echo "config"
    #sudo docker-compose run -d config-service
    echo "registry"
    #sudo docker-compose run -d registry-service
    echo "auth"
    #sudo docker-compose run -d  -e AUTH_PROFILE=test auth-service
    echo "product"
    #sudo docker-compose run -d  -e PRODUCT_PROFILE=test product-service
    echo "shopping"
    #sudo docker-compose run -d  -e SHOPPING_PROFILE=test shopping-service
  fi
fi
exit 0
