REM Author: Dylan Smith
REM Date: 4 July 2016
REM automates build of a Java socket server

REM set up environment variables:
call "C:\Program Files (x86)\Microsoft Visual Studio 14.0\VC\vcvarsall.bat" x86

REM move to current workspace
cd "C:\Users\g13s0714\Desktop\CS Honours\GitHub\JavaIPC\Implementation"

REM compile the java code using standard java compiler
"C:\Program Files (x86)\Java\jdk1.7.0_79\bin\Javac" JavaSocketsServer.java

REM run the java interpreter
"C:\Program Files (x86)\Java\jdk1.7.0_79\bin\Java" JavaSocketsServer


pause
