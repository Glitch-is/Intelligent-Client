@ECHO OFF

ECHO Looking for JDK

SET KEY_NAME=HKLM\SOFTWARE\JavaSoft\Java Development Kit
FOR /F "tokens=3" %%A IN ('REG QUERY "%KEY_NAME%" /v CurrentVersion 2^>NUL') DO SET jdkv=%%A
SET jdk=

IF DEFINED jdkv (
	FOR /F "skip=2 tokens=2*" %%A IN ('REG QUERY "%KEY_NAME%\%jdkv%" /v JavaHome 2^>NUL') DO SET jdk=%%B
) ELSE (
	FOR /F "tokens=*" %%G IN ('DIR /B "%ProgramFiles%\Java\jdk*"') DO SET jdk=%%G
)

SET jdk=%jdk%\bin
SET jre=%jdk%\jre\bin
SET javac="%jdk%\javac.exe"

IF NOT EXIST %javac% (
	javac -version 2>NUL
	IF "%ERRORLEVEL%" NEQ "0" GOTO :notfound
) ELSE (
	GOTO :setpath
)
GOTO :eof

:notfound
ECHO JDK is not installed, please download and install it from:
ECHO http://java.sun.com/javase/downloads
ECHO.
PAUSE
EXIT

:setpath
SET PATH=%jdk%;%jre%;%PATH%
GOTO :eof
