cd /d "%~dp0"
call install.cmd
cd app
if exist vipaccess\target\vipaccess.jar goto run
call mvn clean install

:run
cd vipaccess\target
java -jar vipaccess.jar -debug false -silent false  -mode mobile  -proxyHost localhost   -proxyPort 8888
cd /d "%~dp0"



