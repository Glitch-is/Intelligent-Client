@ECHO OFF

SET cc=java
SET cflags=-jar
SET res=resources
SET dist=RenatusBot.jar

CALL "%res%\FindJDK.bat"

echo Opening %dist%
"%cc%" %cflags% %dist%