@echo off
set "launch4jc=C:\Program Files (x86)\Launch4j\launch4jc.exe"
set "launch4jConfig=launch4j-config.xml"

echo Building the project...
mvn clean compile assembly:single && build-exe.bat && echo Done! || echo Failed