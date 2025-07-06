@echo off
echo ===============================================
echo    Payroll Management System - Build Only
echo ===============================================
echo.
cd /d "%~dp0"

REM Check if MySQL JDBC driver exists
set MYSQL_DRIVER_FOUND=0
for %%f in ("lib\mysql-connector-*.jar") do (
    if exist "%%f" (
        set MYSQL_DRIVER_FOUND=1
        echo [INFO] Found MySQL JDBC Driver: %%f
    )
)

if %MYSQL_DRIVER_FOUND%==0 (
    echo [ERROR] MySQL JDBC Driver not found!
    echo.
    echo Please download the MySQL JDBC driver and place it in the lib/ directory.
    echo Download from: https://dev.mysql.com/downloads/connector/j/
    echo.
    echo For detailed instructions, see MYSQL_SETUP.md
    pause
    exit /b 1
)

REM Clean and create classes directory
if exist "classes" (
    echo [INFO] Cleaning previous build...
    rmdir /s /q "classes"
)
mkdir "classes"

REM Compile Java files
echo [INFO] Compiling Java source files...
javac -d classes -cp "lib/*;src" src\models\*.java src\managers\*.java src\gui\*.java src\database\*.java

if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)

echo [INFO] Build successful!
echo.
pause
