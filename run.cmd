cd /d "%~dp0"
call install.cmd
cd app
call mvn clean install
cd vipaccess\target
java -jar vipaccess.jar -debug false -silent false  -mode mobile  -proxyHost localhost   -proxyPort 8888
cd /d "%~dp0"



