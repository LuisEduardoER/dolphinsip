@echo off

SETLOCAL

SET JAVA_HOME=C:\Program Files\Java\jdk1.6.0_13
SET CLASSPATH="./dist/dip.compiler_0.0.1.jar"
SET CLASSPATH="./dist/dip.components_0.0.1.jar";%CLASSPATH%
SET CLASSPATH="./dist/bcel-5.2.jar";%CLASSPATH%
SET CLASSPATH="./dist/commons-lang-2.3.jar";%CLASSPATH%
SET CLASSPATH="./dist/commons-cli-1.1.jar";%CLASSPATH%
SET CLASSPATH="./dist/log4j-1.2.15.jar";%CLASSPATH%

echo Start compilation process....
"%JAVA_HOME%\bin\java" -Xms256m -Xmx512m -cp %CLASSPATH% by.bsu.fami.etl.compiler.ConsoleCompiler %1 %2 %3 %4 %5 %6 %7 %8 %9
echo Finished compile

ENDLOCAL