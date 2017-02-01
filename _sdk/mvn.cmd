@if defined SDK_DEBUG @( ECHO on ) else @( ECHO off )
@if not defined SDK_HOME call "%~dp0..\install.cmd"
@if defined SDK_DEBUG @echo @%M2_HOME%\bin\mvn --settings  %SDK_HOME%\mvn-user-settings.xml %*
@%M2_HOME%\bin\mvn --settings  %SDK_HOME%\mvn-user-settings.xml %*