@echo off
echo Running database migration to add comprehensive_employee_id column...
echo.
echo Please ensure MySQL is running and you have the correct credentials.
echo.
pause

REM Update the connection details below if needed
set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_USER=root
set MYSQL_PASSWORD=Jisoo@010322
set DATABASE_NAME=payroll_system

echo Connecting to MySQL and running migration...
mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PASSWORD% %DATABASE_NAME% < migrate_add_comprehensive_id.sql

if %errorlevel% equ 0 (
    echo.
    echo Migration completed successfully!
    echo The comprehensive_employee_id column has been added to the employees table.
    echo You can now run the application with the new comprehensive ID features.
) else (
    echo.
    echo Migration failed. Please check your MySQL connection and credentials.
    echo Make sure MySQL is running and the database exists.
)

echo.
pause
