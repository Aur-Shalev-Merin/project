@echo off
REM Compile all Java source files

echo Compiling Java source files...

REM Create bin directory if it doesn't exist
if not exist bin mkdir bin

REM Compile all source files
javac -d bin src/simulation/Position.java src/organisms/Organism.java src/simulation/Grid.java src/simulation/Ecosystem.java src/Main.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo.
    echo To run the simulation, use: run.bat
) else (
    echo Compilation failed. Please check the errors above.
)

pause
