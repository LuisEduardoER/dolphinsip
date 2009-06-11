@echo off

SETLOCAL

SET JAVA_HOME=C:\Program Files\Java\jdk1.6.0_07
SET CLASSPATH="./dist/dip.scheduler_0.0.1.jar";
SET CLASSPATH="./dist/dip.engine_0.0.1.jar";%CLASSPATH%
SET CLASSPATH="./dist/dip.checker_0.0.1.jar";%CLASSPATH%
SET CLASSPATH="./dist/dip.compiler_0.0.1.jar";%CLASSPATH%
SET CLASSPATH="./dist/dip.components_0.0.1.jar";%CLASSPATH%
SET CLASSPATH="./dist/bcel-5.2.jar";%CLASSPATH%
SET CLASSPATH="./dist/commons-lang-2.3.jar";%CLASSPATH%
SET CLASSPATH="./dist/commons-cli-1.1.jar";%CLASSPATH%
SET CLASSPATH="./dist/log4j-1.2.15.jar";%CLASSPATH%
SET CLASSPATH="./dist/hsqldb.jar";%CLASSPATH%
SET CLASSPATH="./dist/derby.jar";%CLASSPATH%
SET CLASSPATH="./dist/derbyclient.jar";%CLASSPATH%

echo Start Dolphins Integration Platform Scheduler....
"%JAVA_HOME%\bin\java" -Xms256m -Xmx512m -Dscheduler.log.Logger=by.bsu.fami.etl.scheduler.log.DBLogger -cp %CLASSPATH% by.bsu.fami.etl.scheduler.ConsoleScheduler %1 %2 %3 %4 %5 %6 %7 %8 %9
echo Scheduler closed

ENDLOCAL