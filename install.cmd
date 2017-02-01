@if defined SDK_DEBUG @( ECHO on ) else @( ECHO off )

set "JAVA_HOME=C:\Program Files\Java\jdk1.8.0_121"
set "M2_HOME=%~dp0_maven"

set "SDK_HOME=%~dp0_sdk"
path=%SDK_HOME%;%M2_HOME%\bin;%JAVA_HOME%\bin;%ECLIPSE_HOME%;C:\Windows;C:\Windows\system32;
