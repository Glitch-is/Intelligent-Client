@ECHO OFF

SET cc=java
SET cflags=-jar
SET res=resources
SET dist="Intelligent Client.jar"

CALL "%res%\FindJDK.bat"

echo Opening %dist%
"%cc%" %cflags% %dist%