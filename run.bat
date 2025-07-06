@echo off
echo Compiling Payroll Management System GUI...
cd /d "%~dp0"

REM Check if MySQL JDBC driver exists (any version)
set MYSQL_DRIVER_FOUND=0
for %%f in ("lib\mysql-connector-*.jar") do (
    if exist "%%f" (
        set MYSQL_DRIVER_FOUND=1
        echo Found MySQL JDBC Driver: %%f
    )
)

if %MYSQL_DRIVER_FOUND%==0 (
    echo MySQL JDBC Driver not found!
    echo Please download the MySQL JDBC driver manually and place it in the lib/ directory.
    echo You can download it from: https://dev.mysql.com/downloads/connector/j/
    echo.
    echo For detailed instructions, see MYSQL_SETUP.md
    pause
    exit /b 1
)

REM Create classes directory if it doesn't exist
if not exist "classes" mkdir classes

REM Compile all Java files with MySQL driver in classpath
echo Compiling Java files...
javac -d classes -cp "lib/*;src" src\models\*.java src\managers\*.java src\gui\*.java src\database\*.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo.
echo Checking database configuration...
if exist "database.config" (
    echo Found database.config file.
    echo To apply configuration, run: configure-database.bat
    echo.
) else (
    echo Note: For MySQL Workbench integration, see MYSQL_SETUP.md
    echo.
)
echo Running application...

REM Run the application with MySQL driver in classpath
java -cp "lib/*;classes" gui.PayrollManagementSystemGUI

pause
