REM Author: Dylan Smith
REM Date: 19 March 2016
REM automates builds

REM set up environment variables:
call "C:\Program Files (x86)\Microsoft Visual Studio 14.0\VC\vcvarsall.bat" x86

REM move to current workspace
cd "C:\Users\g13s0714\Desktop\CS Honours\GitHub\JavaIPC\Implementation"

REM compile the java code using standard java compiler
"C:\Program Files (x86)\Java\jdk1.7.0_79\bin\Javac" WindowsIPC.java

REM create the header file
"C:\Program Files (x86)\Java\jdk1.7.0_79\bin\Javah" -jni -classpath "C:\Users\g13s0714\Desktop\CS Honours\GitHub\JavaIPC\Implementation"  WindowsIPC

REM run the C compiler
 "C:\Program Files (x86)\Microsoft Visual Studio 14.0\VC\bin\cl"  -I "C:\Program Files\Java\jdk1.8.0_45\include" -I "C:\Program Files\Java\jdk1.8.0_45\include\win32" -LD "C:\Users\g13s0714\Desktop\CS Honours\GitHub\JavaIPC\Implementation\WindowsIPC.c"  -FeWindowsIPC.dll

REM run the java interpreter
"C:\Program Files (x86)\Java\jdk1.7.0_79\bin\Java" WindowsIPC

pause
