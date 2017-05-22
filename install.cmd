@if defined SDK_DEBUG @( ECHO on ) else @( ECHO off )
set "BACKUP_HOME=c:\backup"
set "JAVA_HOME=C:\Program Files\Java\jdk1.8.0_121"
set "ECLIPSE_HOME=e:\eclipse.neon"
set "SDK_HOME=%~dp0_sdk"
set "M2_HOME=%~dp0_maven"
path=%SDK_HOME%;%M2_HOME%\bin;%JAVA_HOME%\bin;%ECLIPSE_HOME%;%path%