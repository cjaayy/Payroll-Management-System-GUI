@echo off
echo Checking MySQL Server Status...
echo.

REM Check if MySQL service is running
sc query mysql80 >nul 2>&1
if %errorlevel%==0 (
    echo MySQL80 service found. Checking status...
    sc query mysql80 | find "RUNNING" >nul
    if %errorlevel%==0 (
        echo ✓ MySQL80 service is RUNNING
    ) else (
        echo ✗ MySQL80 service is NOT RUNNING
        echo Starting MySQL80 service...
        net start mysql80
    )
) else (
    REM Try MySQL57
    sc query mysql57 >nul 2>&1
    if %errorlevel%==0 (
        echo MySQL57 service found. Checking status...
        sc query mysql57 | find "RUNNING" >nul
        if %errorlevel%==0 (
            echo ✓ MySQL57 service is RUNNING
        ) else (
            echo ✗ MySQL57 service is NOT RUNNING
            echo Starting MySQL57 service...
            net start mysql57
        )
    ) else (
        REM Try generic MySQL service
        sc query mysql >nul 2>&1
        if %errorlevel%==0 (
            echo MySQL service found. Checking status...
            sc query mysql | find "RUNNING" >nul
            if %errorlevel%==0 (
                echo ✓ MySQL service is RUNNING
            ) else (
                echo ✗ MySQL service is NOT RUNNING
                echo Starting MySQL service...
                net start mysql
            )
        ) else (
            echo ✗ No MySQL service found!
            echo Please install MySQL Server first.
            echo Download from: https://dev.mysql.com/downloads/installer/
        )
    )
)

echo.
echo Testing MySQL connection on port 3306...
netstat -an | find "3306" >nul
if %errorlevel%==0 (
    echo ✓ MySQL is listening on port 3306
) else (
    echo ✗ MySQL is not listening on port 3306
)

echo.
echo For detailed setup instructions, see: MYSQL_TROUBLESHOOTING.md
echo.
pause
