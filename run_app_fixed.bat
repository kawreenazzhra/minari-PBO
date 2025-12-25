@echo off
echo Stopping any existing Java processes...
taskkill /F /IM java.exe >nul 2>&1

echo Starting MINARI Application...
echo Database: Persistent (data/minari.mv.db)
echo Port: 8080
echo.
echo Please wait for "Started MinariApplication"...
echo.

java -jar target/MINARI-0.0.1-SNAPSHOT.jar --spring.config.location=file:./src/main/resources/application.properties

pause
