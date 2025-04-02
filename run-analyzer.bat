@echo off
cd weather-analyzer
call mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
pause
