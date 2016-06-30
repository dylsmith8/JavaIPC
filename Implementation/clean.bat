REM Author: Dylan Smith
REM Date: 30 June 2016

REM script that cleans up all generated files if you want to perform a fresh compile of everything

REM set up environment variables:
call "C:\Program Files (x86)\Microsoft Visual Studio 14.0\VC\vcvarsall.bat" x86

REM move to current workspace
cd "C:\Users\g13s0714\Desktop\CS Honours\GitHub\JavaIPC\Implementation"

del *.class
del WindowsIPC.dll
del WindowsIPC.lib
del WindowsIPC.obj
del WindowsIPC.exp
del WindowsIPC.h

pause
