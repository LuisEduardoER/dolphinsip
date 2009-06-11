@echo off

SETLOCAL

SET JAVA_HOME=C:\Program Files\Java\jdk1.6.0_07
SET CLASSPATH="./dist/dip.engine_0.0.1.jar"
SET CLASSPATH="./dist/dip.compiler_0.0.1.jar";%CLASSPATH%
SET CLASSPATH="./dist/dip.components_0.0.1.jar";%CLASSPATH%
SET CLASSPATH="./dist/bcel-5.2.jar";%CLASSPATH%
SET CLASSPATH="./dist/commons-lang-2.3.jar";%CLASSPATH%
SET CLASSPATH="./dist/commons-cli-1.1.jar";%CLASSPATH%
SET CLASSPATH="./dist/log4j-1.2.15.jar";%CLASSPATH%
SET CLASSPATH="./dist/hsqldb.jar";%CLASSPATH%
SET CLASSPATH="./dist/derby.jar";%CLASSPATH%
SET CLASSPATH="./dist/derbyclient.jar";%CLASSPATH%

echo Start Dolphins Integration Platform Engine....
"%JAVA_HOME%\bin\java" -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Xms256m -Xmx512m -cp %CLASSPATH% by.bsu.fami.etl.engine.ConsoleEngine -jmx %1 %2 %3 %4 %5 %6 %7 %8 %9
echo Engine closed

ENDLOCAL