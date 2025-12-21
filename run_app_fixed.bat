@echo off
echo Stopping any existing Java processes...
taskkill /F /IM java.exe >nul 2>&1

echo Starting MINARI Application...
echo Database: H2 In-Memory (jdbc:h2:mem:final_db)
echo Port: 8081
echo.
echo Please wait for "Started MinariApplication" or "ACCEPTING_TRAFFIC"...
echo.

java -jar target/MINARI-0.0.1-SNAPSHOT.jar --spring.config.location=file:./src/main/resources/application.properties --spring.datasource.url=jdbc:h2:mem:final_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE

pause
