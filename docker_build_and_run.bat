@echo off
REM #############################################
REM ### Script to up Docker's containers (Win) ###
REM #############################################

REM === Application ===
rmdir /s /q target
call mvn clean install -Pdocker -DskipTests

REM === Docker ===
echo.
echo ##############
echo ### Docker ###
echo ##############
echo.

docker rmi -f rodrigoamora/rodrigo-springboot

docker-compose down
docker-compose down --rmi all

echo.
echo ###########################
echo ### Building images.... ###
echo ###########################
echo.

docker-compose build

echo.
echo ########################
echo ### Uping containers ###
echo ########################
echo.

docker-compose up -d

echo.
echo ###############################
echo ### Application running!!!! ###
echo ###############################

