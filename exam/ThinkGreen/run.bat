@echo off
setlocal

set JAVAFX_PATH=D:\java\exam\javafx-sdk\lib
set MYSQL_JAR=D:\java\exam\ThinkGreen\lib\mysql-connector-j-8.0.33.jar
set SRC=src
set OUT=out

:: Clean output
if exist %OUT% rmdir /s /q %OUT%
mkdir %OUT%

:: Copy FXML and CSS files to output
echo Copying resources...
xcopy /s /q /y "%SRC%\ui\*.fxml" "%OUT%\ui\" >nul 2>&1
xcopy /s /q /y "%SRC%\ui\*.css" "%OUT%\ui\" >nul 2>&1

:: Compile all Java files
echo Compiling...
dir /s /b %SRC%\*.java > sources.txt
javac --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -cp "%MYSQL_JAR%" -d %OUT% @sources.txt

if %ERRORLEVEL% neq 0 (
    echo.
    echo *** COMPILATION FAILED ***
    del sources.txt
    pause
    exit /b 1
)
del sources.txt

:: Run
echo Running...
java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -cp "%OUT%;%MYSQL_JAR%" app.Main

endlocal
