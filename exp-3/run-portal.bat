@echo off
title Student Profile Portal Launcher
echo ============================================================
echo   Starting Student Profile Portal on Apache Tomcat 10.1
echo ============================================================
echo.

set "CATALINA_HOME=C:\apache-tomcat-10.1.61"
set "JAVA_HOME=C:\Program Files\Java\jdk-21"

if not exist "%CATALINA_HOME%\bin\startup.bat" (
    echo [ERROR] Apache Tomcat not found at %CATALINA_HOME%
    pause
    exit /b 1
)

echo Deploying latest WAR to Tomcat webapps...
copy /Y "%~dp0target\StudentProfilePortal.war" "%CATALINA_HOME%\webapps\" >nul

echo Starting Apache Tomcat in the background...
start "Apache Tomcat 10.1" /min "%CATALINA_HOME%\bin\startup.bat"

echo Waiting for Tomcat to initialize...
timeout /t 3 /nobreak >nul

echo Opening browser at http://localhost:8080/StudentProfilePortal ...
start "" "http://localhost:8080/StudentProfilePortal"

echo.
echo ============================================================
echo   Application is now RUNNING!
echo   URL: http://localhost:8080/StudentProfilePortal
echo ============================================================
