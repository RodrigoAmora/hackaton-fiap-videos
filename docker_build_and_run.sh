#!/bin/bash

#############################################
### Shellscript to up Docker's containers ###
#############################################

source ~/.bash_profile

### Application ###
rm -rf target/
export $(grep -v '^#' .env | xargs)
mvn clean install -Pprod -DskipTests

### Docker ###
echo -e "\n\n"
echo -e "\033[01;32m##############\033[01;32m"
echo -e "\033[01;32m### Docker ###\033[01;32m"
echo -e "\033[01;32m##############\033[01;32m"
echo -e "\n"

sudo docker rmi -f rodrigoamora/rodrigo-springboot

docker-compose down
docker-compose down --rmi rodrigoamora/rodrigo-springboot


if [[ $(uname -s) == "Darwin" ]]; then
  echo -e "\n"
  echo "Running on MacOS, forcing linux/amd64"
  echo -e "\n"

  echo -e "\n"
  echo -e "\033[01;32m### Building images.... ###\033[01;32m"
  echo -e "\n"

  sudo docker-compose -f docker-compose-mac.yml build --no-cache

  echo -e "\n"
  echo -e "\033[01;32m### Upping containers ###\033[01;32m"
  echo -e "\n"

  sudo docker-compose -f docker-compose-mac.yml up -d --force-recreate

else
  echo -e "\n"
  echo "Running on Linux/Windows"
  echo -e "\n"

  echo -e "\n"
  echo -e "\033[01;32m### Building images.... ###\033[01;32m"
  echo -e "\n"

  sudo docker-compose build --no-cache

  echo -e "\n"
  echo -e "\033[01;32m### Upping containers ###\033[01;32m"
  echo -e "\n"

  sudo docker-compose up -d --force-recreate
fi

echo -e "\n"
echo -e "\033[01;32m###############################\033[01;32m"
echo -e "\033[01;32m### Application running!!!! ###\033[01;32m"
echo -e "\033[01;32m###############################\033[01;32m"
