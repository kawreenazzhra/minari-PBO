@echo off
echo ==========================================
echo Starting Minari Application...
echo ==========================================

REM Check for Java version issue
java -version 2> java_ver.tmp
findstr "version \"25" java_ver.tmp >nul
if %errorlevel% equ 0 (
    echo [WARNING] You are using Java 25. This version is very new and causes build errors.
    echo [WARNING] If the application fails to start, please install Java 21 or Java 17.
    echo.
)
del java_ver.tmp

REM Try to run using Maven Wrapper (Spring Boot Plugin)
call .\mvnw.cmd spring-boot:run -DskipTests

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] The application failed to start.
    echo [Possible Cause] Incompatible Java version - Java 25 - conflicting with Lombok.
    echo [Solution] Please install JDK 21 and try again.
    pause
)
