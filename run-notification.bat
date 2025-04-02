@echo off
cd alert-notification
call mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
pause
