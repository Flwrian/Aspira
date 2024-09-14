@echo off
set "launch4jc=C:\Program Files (x86)\Launch4j\launch4jc.exe"
set "launch4jConfig=launch4j-config.xml"

"%launch4jc%" "%launch4jConfig%" && echo Done! || echo Failed