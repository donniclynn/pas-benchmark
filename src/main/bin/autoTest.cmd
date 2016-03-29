@echo off

if "%CTG_HOME%" == "" (set CTG_HOME=D:\java\workspaceCache\cache-benchmark\cache-benchmark-release)
set CTG_START=%CTG_HOME%/bin/start.jar
set CLASS_PATH=../conf; 
set CTG_CMD=java  -classpath %CLASS_PATH% -jar %CTG_START%
%CTG_CMD%
