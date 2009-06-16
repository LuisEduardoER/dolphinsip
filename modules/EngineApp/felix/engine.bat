@echo off

SETLOCAL

SET JAVA_HOME=C:\Program Files\Java\jdk1.6.0_07
SET CLASSPATH="engine.jar"

echo Start Dolphins Integration Platform Engine....
"%JAVA_HOME%\bin\java" -Xms64m -Xmx256m -cp %CLASSPATH% com.dsc.dip.etl.engine.app.felix.Main %1 %2 %3 %4 %5 %6 %7 %8 %9
echo Engine closed

pause

ENDLOCAL