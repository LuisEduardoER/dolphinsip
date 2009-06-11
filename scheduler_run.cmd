@echo off

SETLOCAL

call scheduler -log4j repository/config/scheduler.log4j.properties -conf repository/config/scheduler.config

ENDLOCAL