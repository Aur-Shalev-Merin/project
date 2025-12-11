@echo off
echo ===============================================
echo   Ecosystem Simulation Runner
echo ===============================================
echo.

echo [1/3] Compiling Java code...
javac -d bin src\Main.java src\simulation\*.java src\organisms\*.java src\organisms\plants\*.java src\organisms\animals\*.java

if %errorlevel% neq 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo [2/3] Running simulation...
java -cp bin Main

if %errorlevel% neq 0 (
    echo ERROR: Simulation failed!
    pause
    exit /b 1
)

echo.
echo [3/3] Generating visualizations...
python visualize_ecosystem.py

if %errorlevel% neq 0 (
    echo ERROR: Visualization failed!
    pause
    exit /b 1
)

echo.
echo ===============================================
echo   SUCCESS! Check the PNG files.
echo ===============================================
pause
