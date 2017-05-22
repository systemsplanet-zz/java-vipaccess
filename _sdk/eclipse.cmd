@echo on
cd /d "%~dp0.."
call install.cmd
start "eclipse" /D "%SDK_HOME%" "%ECLIPSE_HOME%\eclipse.exe"
exit /b 0