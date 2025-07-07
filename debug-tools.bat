@echo off
echo ===============================================
echo    Payroll Management System - Debug Tools
echo ===============================================
cd /d "%~dp0"

REM Check if MySQL JDBC driver exists
set MYSQL_DRIVER_FOUND=0
for %%f in ("lib\mysql-connector-*.jar") do (
    if exist "%%f" (
        set MYSQL_DRIVER_FOUND=1
    )
)

if %MYSQL_DRIVER_FOUND%==0 (
    echo [ERROR] MySQL JDBC Driver not found!
    echo Please run run.bat first to set up the environment.
    pause
    exit /b 1
)

REM Ensure classes directory exists
if not exist "classes" (
    echo [INFO] Classes directory not found. Building application first...
    call run.bat
    if %errorlevel% neq 0 (
        echo [ERROR] Build failed!
        pause
        exit /b 1
    )
)

echo.
echo Available debug tools:
echo [1] Database Debug - Inspect database contents
echo [2] Authentication Debug - Test login functionality
echo [3] HR Access Test - Test HR user permissions
echo [4] HR User Setup - Create/reset HR users
echo [5] Password Reset - Reset admin password
echo [0] Exit
echo.

set /p choice="Enter your choice (0-5): "

if "%choice%"=="1" (
    echo [INFO] Running Database Debug...
    java -cp "lib/*;classes" utils.DatabaseDebug
) else if "%choice%"=="2" (
    echo [INFO] Running Authentication Debug...
    java -cp "lib/*;classes" utils.AuthDebug
) else if "%choice%"=="3" (
    echo [INFO] Running HR Access Test...
    java -cp "lib/*;classes" utils.HRAccessTest
) else if "%choice%"=="4" (
    echo [INFO] Running HR User Setup...
    java -cp "lib/*;classes" utils.HRUserSetup
) else if "%choice%"=="5" (
    echo [INFO] Running Password Reset...
    java -cp "lib/*;classes" utils.PasswordReset
) else if "%choice%"=="0" (
    echo [INFO] Exiting...
    exit /b 0
) else (
    echo [ERROR] Invalid choice!
)

echo.
pause
