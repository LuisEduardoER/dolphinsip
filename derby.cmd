@echo off

SET DERBY_INSTALL=.\utils\derby
SET CLASSPATH=%DERBY_INSTALL%\lib\derby.jar;%DERBY_INSTALL%\lib\derbytools.jar
SET CLASSPATH=%DERBY_INSTALL%\lib\derbyclient.jar;%CLASSPATH%;
call %DERBY_INSTALL%\bin\setEmbeddedCP.bat
java org.apache.derby.tools.sysinfo

java org.apache.derby.tools.ij