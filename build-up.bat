@echo off
setlocal enabledelayedexpansion

:: === CONFIGURATION ===
set launch4jc="C:\Program Files (x86)\Launch4j\launch4jc.exe"
set engines_dir=.\engines

:: === Choix du moteur
set /p versionType="Do you want to build dev or main version? (dev/main): "
if /i "%versionType%"=="dev" (
    set engine=dev
) else if /i "%versionType%"=="main" (
    set engine=main
) else (
    echo Invalid input. Please enter 'dev' or 'main'.
    exit /b 1
)

:: === Compilation Java
echo Building the project...
call mvn clean compile assembly:single || exit /b 1

@REM outputs to target/chess-engine.jar
set jar_file=.\target\chess-engine.jar

:: === Création de l'exe avec Launch4j
echo Building EXE with Launch4j...
call %launch4jc% "%engine%.xml" || exit /b 1

:: === Lecture de la version actuelle
set versionFile=%engine%_version.txt
if not exist %versionFile% (
    echo 0 > %versionFile%
)

set /p lastVersion=<%versionFile%
set /a newVersion=%lastVersion% + 1

echo New version : %newVersion%
echo %newVersion% > %versionFile%
echo Version updated to %newVersion%

:: === Renommage de l'exe
set output_file=Aspira_%engine%_%newVersion%.exe
move Aspira_%engine%.exe %engines_dir%\%output_file%

:: === Renommage du jar
set jar_output_file=Aspira_%engine%_%newVersion%.jar
move %jar_file% %engines_dir%\%jar_output_file%

:: Signe le fichier
echo Signing the file...
python sign.py %engines_dir%\%output_file%
echo File signed

:: Upload .exe + .sig
curl -X POST http://localhost:8000/upload_engine ^
  -H "X-API-Key: %API_KEY%" ^
  -F "file=@%engines_dir%\%output_file%" ^
  -F "signature=@%engines_dir%\%output_file%.sig" ^
  -F "version=%newVersion%" ^
  -F "engine_type=%engine%" ^
  -F "key=Aspira.pem"

:: sign jar file
echo Signing the jar file...
python sign.py %engines_dir%\%jar_output_file%
echo Jar file signed

:: Upload jar + .sig

curl -X POST http://localhost:8000/upload_engine ^
  -H "X-API-Key: %API_KEY%" ^
  -F "file=@%engines_dir%\%jar_output_file%" ^
  -F "signature=@%engines_dir%\%jar_output_file%.sig" ^
  -F "version=%newVersion%" ^
  -F "engine_type=%engine%" ^
  -F "key=Aspira.pem"

echo Build completed successfully : %output_file%
endlocal
