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
SET CLASSPATH="./dist/commons-logging-1.1.jar";%CLASSPATH%
SET CLASSPATH="./dist/axiom-api-1.2.5.jar";%CLASSPATH%
SET CLASSPATH="./dist/axiom-impl-1.2.5.jar";%CLASSPATH%
SET CLASSPATH="./dist/axis2-kernel-1.3.jar";%CLASSPATH%
SET CLASSPATH="./dist/commons-codec-1.3.jar";%CLASSPATH%
SET CLASSPATH="./dist/commons-httpclient-3.0.1.jar";%CLASSPATH%
SET CLASSPATH="./dist/jaxen-1.1.1.jar";%CLASSPATH%
SET CLASSPATH="./dist/wsdl4j-1.6.2.jar";%CLASSPATH%
SET CLASSPATH="./dist/XmlSchema-1.3.2.jar";%CLASSPATH%
SET CLASSPATH="./dist/hsqldb.jar";%CLASSPATH%
SET CLASSPATH="./dist/derby.jar";%CLASSPATH%
SET CLASSPATH="./dist/derbyclient.jar";%CLASSPATH%

echo Start Dolphins Integration Platform Engine....
"%JAVA_HOME%\bin\java" -Xms256m -Xmx512m -cp %CLASSPATH% by.bsu.fami.etl.engine.ConsoleEngine %1 %2 %3 %4 %5 %6 %7 %8 %9
echo Engine closed

ENDLOCAL