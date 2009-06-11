@echo off

SETLOCAL

SET JAVA_HOME=C:\Program Files\Java\jdk1.6.0_07
SET ANT_HOME=.\utils\ant

set ANT_OPTS=-Xmx1024m

echo Starting DIP build
echo Target: %1
title %1
rem call %ANT_HOME%\bin\ant -d -logfile build.log -buildfile build.xml %1
call %ANT_HOME%\bin\ant -buildfile build.xml %1
echo Finished DIP build

ENDLOCAL