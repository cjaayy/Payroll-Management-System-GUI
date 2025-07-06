@echo off
echo MySQL Database Configuration Wizard
echo ====================================
echo.

REM Compile the configuration wizard
echo Compiling database configuration wizard...
javac -cp "lib/*;src" -d classes src/database/DatabaseConfigWizard.java src/database/DatabaseConfig.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    echo Make sure the MySQL JDBC driver is in the lib/ directory.
    pause
    exit /b 1
)

echo.
echo Running database configuration wizard...
echo.
java -cp "lib/*;classes" database.DatabaseConfigWizard

echo.
echo After updating the configuration, you can run the main application with:
echo run.bat
echo.
pause
