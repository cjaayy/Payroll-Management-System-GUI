@echo off
echo ===============================================
echo    Payroll Management System - With DB Config
echo ===============================================
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
    echo Please download the MySQL JDBC driver and place it in the lib/ directory.
    echo Download from: https://dev.mysql.com/downloads/connector/j/
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
javac -d classes -cp "lib/*;src" src\models\*.java src\managers\*.java src\gui\*.java src\database\*.java src\utils\*.java src\test\*.java

if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)

echo [INFO] Compilation successful!

REM Get database credentials from user
echo.
echo [SETUP] Database Configuration Required:
echo Please enter your MySQL database credentials:
echo.
set /p DB_USER="Enter MySQL username (default: root): "
if "%DB_USER%"=="" set DB_USER=root

set /p DB_PASS="Enter MySQL password (leave blank if no password): "

echo.
echo [INFO] Starting Payroll Management System...
echo [INFO] Using database credentials: %DB_USER%@localhost
echo.

REM Run the application with database credentials
java -Ddb.username=%DB_USER% -Ddb.password=%DB_PASS% -cp "lib/*;classes" gui.PayrollManagementSystemGUI
pause
