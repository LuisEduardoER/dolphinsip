@echo off

SETLOCAL

call compiler -sourcedir repository\src -destdir repository\bin my\MyRule.rule

ENDLOCAL