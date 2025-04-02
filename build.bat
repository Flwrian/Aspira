@echo off
setlocal

set launch4jc=C:\Program Files (x86)\Launch4j\launch4jc.exe
set /p version="Do you want to build dev or main version? (dev/main): "
if /i "%version%"=="dev" (
    set engine=dev
) else if /i "%version%"=="main" (
    set engine=main
) else (
    echo Invalid input. Please enter 'dev' or 'main'.
    exit /b 1
)

echo Building the project...
call mvn clean compile assembly:single || exit /b 1

set launch4jConfig=%engine%.xml

echo Building EXE...
call "%launch4jc%" "%launch4jConfig%" || exit /b 1


type %engine%.txt

endlocal
