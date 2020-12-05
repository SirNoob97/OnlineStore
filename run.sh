#!/bin/bash

sudo docker-compose run -d postgresql
sudo docker-compose run -d config-service
sudo docker-compose run -d registry-service

while [ $# -gt 0 ]; do
  if [[ $1 =~ ^((-{1})([p|P]$|[a|A]$|[s|S]$)) ]]; then
    opt=$1; shift;
    current_arg=${1,,}

    if [[ $current_arg =~ ^-{1,2}.* ]]; then
      echo "You may have left an argument blank. Double check your command."
    fi

        if [[ $current_arg == "test" || $current_arg == "postgresql" ]] ; then

      case $opt in
        "-a"|"-A") AUTH_P=$current_arg; shift;;
        "-p"|"-P") PRODUCT_P=$current_arg; shift;;
        "-s"|"-S") SHOPPING_P=$current_arg; shift;;
        *) echo "invalid option \""$opt"\"" >&2
        exit 1;;
      esac

    else
      echo "Invalid Agument"; exit 1
    fi

  else
    echo "Invalid format"; exit 1
  fi
done

if [[ $PRODUCT_P == "" || $AUTH_P == "" || $SHOPPING_P == "" ]]; then
  echo "Arguments are required!!" >&2
  exit 1
else
  echo "Execution of Online Store."
  sudo docker-compose run -d  -e AUTH_PROFILE=$AUTH_P auth-service
  sudo docker-compose run -d  -e PRODUCT_PROFILE=$PRODUCT_P product-service
  sudo docker-compose run -d  -e SHOPPING_PROFILE=$SHOPPING_P shopping-service
fi

