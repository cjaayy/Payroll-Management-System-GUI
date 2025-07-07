@echo off
echo ===============================================
echo    Payroll Management System GUI
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
javac -d classes -cp "lib/*;src" src\models\*.java src\managers\*.java src\gui\*.java src\database\*.java src\utils\*.java src\test\*.java

if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)

echo [INFO] Compilation successful!

REM Check database configuration
if exist "database.config" (
    echo [NOTE] Database configuration file found.
) else (
    echo [NOTE] No database.config file found. See MYSQL_SETUP.md for setup instructions.
)

REM Run the application
echo [INFO] Starting Payroll Management System...
echo.
echo [SETUP] Database Configuration:
echo If you need to set database credentials, you can:
echo 1. Set system properties: -Ddb.username=root -Ddb.password=yourpassword
echo 2. Or modify src/database/DatabaseConfig.java
echo.
echo [INFO] Using default database settings (root with configured password)
echo If connection fails, use run-with-db.bat for interactive database setup.
echo.
java -cp "lib/*;classes" gui.PayrollManagementSystemGUI
pause
