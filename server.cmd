@echo off

SETLOCAL

SET JAVA_HOME=C:\Program Files\Java\jdk1.6.0_07
SET JETTY_HOME=.\utils\jetty
SET SERVER_HOME=.\jetty

SET CLASSPATH="%JETTY_HOME%/start.jar";
SET CLASSPATH="./dist/jetty-6.1.1.jar";%CLASSPATH%
SET CLASSPATH="./dist/jetty-util-6.1.1.jar";%CLASSPATH%
SET CLASSPATH="./dist/servlet-api-2.5-6.1.1.jar";%CLASSPATH%
SET CLASSPATH="./dist/ant-1.6.5.jar";%CLASSPATH%
SET CLASSPATH="./dist/core-3.1.1.jar";%CLASSPATH%
SET CLASSPATH="./dist/jsp-2.1.jar";%CLASSPATH%
SET CLASSPATH="./dist/jsp-api-2.1.jar";%CLASSPATH%
SET CLASSPATH="./dist/derby.jar";%CLASSPATH%
SET CLASSPATH="./dist/derbyclient.jar";%CLASSPATH%

echo Start Dolphins Integration Platform Server....
"%JAVA_HOME%\bin\java" -cp %CLASSPATH% org.mortbay.start.Main %SERVER_HOME%\config\jetty.xml
echo Server close

ENDLOCAL