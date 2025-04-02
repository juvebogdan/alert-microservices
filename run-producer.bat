@echo off
cd weather-producer
call mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
pause
