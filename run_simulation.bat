@echo off
echo ===============================================
echo   Ecosystem Simulation Runner
echo ===============================================
echo.

echo [1/5] Compiling Java code...
javac -d bin src\Main.java src\JacobianAnalysis.java src\TrophicLevelAnalysis.java src\simulation\*.java src\organisms\*.java src\organisms\plants\*.java src\organisms\animals\*.java src\models\*.java

if %errorlevel% neq 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo [2/5] Running simulation...
java -cp bin Main

if %errorlevel% neq 0 (
    echo ERROR: Simulation failed!
    pause
    exit /b 1
)

echo.
echo [3/5] Generating visualizations...
python visualize_ecosystem.py

if %errorlevel% neq 0 (
    echo ERROR: Visualization failed!
    pause
    exit /b 1
)

echo.
echo [4/5] Running Trophic Level Analysis...
echo.
java -cp bin TrophicLevelAnalysis

echo.
echo [5/5] Running Jacobian Analysis (Interactive)...
echo.
echo You can now analyze any 2-species pair.
echo Press Ctrl+C to skip, or select species pairs to analyze.
echo.
java -cp bin JacobianAnalysis

echo.
echo ===============================================
echo   SUCCESS! Check the PNG files and analysis TXT files.
echo ===============================================
pause
