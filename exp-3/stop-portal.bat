@echo off
title Stop Student Profile Portal
echo Stopping Apache Tomcat 10.1...
set "CATALINA_HOME=C:\apache-tomcat-10.1.61"
set "JAVA_HOME=C:\Program Files\Java\jdk-21"
call "%CATALINA_HOME%\bin\shutdown.bat"
echo Apache Tomcat has been stopped.
timeout /t 2 /nobreak >nul
