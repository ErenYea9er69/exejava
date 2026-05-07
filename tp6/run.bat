@echo off
REM ============================================
REM  Gestion Etudiants - Compile and Run Script
REM ============================================

set JAVA_HOME=C:\Program Files\Java\jdk-25
set JAVAFX_PATH=d:\java\tp fxml\javafx-sdk\lib
set MYSQL_JAR=d:\java\tp6\lib\mysql-connector-j-8.0.33.jar
set OUTPUT=d:\java\tp6\bin

echo [1/3] Nettoyage...
if exist "%OUTPUT%" rmdir /s /q "%OUTPUT%"
mkdir "%OUTPUT%"

echo [2/3] Compilation...
"%JAVA_HOME%\bin\javac" --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -cp "%MYSQL_JAR%" -d "%OUTPUT%" *.java
if %errorlevel% neq 0 (
    echo ERREUR: La compilation a echoue !
    pause
    exit /b 1
)

echo [3/3] Copie du fichier FXML...
copy /y Etudiant.fxml "%OUTPUT%\"

echo.
echo Lancement de l'application...
"%JAVA_HOME%\bin\java" --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -cp "%OUTPUT%;%MYSQL_JAR%" MainApplication

pause
