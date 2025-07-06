@echo off
echo ===============================================
echo    Payroll Management System - Clean Build
echo ===============================================
echo.
cd /d "%~dp0"

REM Remove compiled classes
if exist "classes" (
    echo [INFO] Removing compiled classes...
    rmdir /s /q "classes"
    echo [INFO] Compiled classes removed.
) else (
    echo [INFO] No compiled classes to remove.
)

REM Remove temporary files
if exist "*.tmp" (
    echo [INFO] Removing temporary files...
    del /q "*.tmp"
)

REM Remove backup files
if exist "*.bak" (
    echo [INFO] Removing backup files...
    del /q "*.bak"
)

echo.
echo [INFO] Cleanup complete!
echo.
pause
