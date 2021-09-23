rem Author: Dylan Smith 
rem Date: 8 March 2017

REM set up environment variables:
call "C:\Program Files (x86)\Microsoft Visual Studio 14.0\VC\vcvarsall.bat" x86

cd "C:\Users\Dylan\Documents\GitHub\JavaIPC\Windows Implementation"
"C:\Program Files (x86)\Java\jdk1.7.0_10\bin\Javac" WindowsIPC.java
"C:\Program Files (x86)\Java\jdk1.7.0_10\bin\Javah" -jni -classpath "C:\Users\Dylan\Documents\GitHub\JavaIPC\Windows Implementation" WindowsIPC
"C:\Program Files (x86)\Microsoft Visual Studio 14.0\VC\bin\cl" -I "C:\Program Files\Java\jdk1.8.0_65\include" -I "C:\Program Files\Java\jdk1.8.0_65\include\win32" -LD "C:\Users\Dylan\Documents\GitHub\JavaIPC\Windows Implementation\WindowsIPC.c" -FeWindowsIPC.dll
"C:\Program Files (x86)\Java\jdk1.7.0_10\bin\Java" WindowsIPC
pause 