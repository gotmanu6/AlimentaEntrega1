@echo off
setlocal
cd /d "%~dp0"
set "JAVA_HOME=%~dp0.java21"
if not exist "%JAVA_HOME%\bin\java.exe" (
  echo Java 21 nao encontrado em "%JAVA_HOME%".
  exit /b 1
)
set "PATH=%JAVA_HOME%\bin;%PATH%"
if "%~1"=="--verificar" (
  call mvnw.cmd --version
  exit /b
)
call mvnw.cmd spring-boot:run
exit /b
