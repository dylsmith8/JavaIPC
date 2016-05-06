rem Author: Dylan Smith
rem Date: 6 May 2016
rem Script that automates file compilation

cd "C:\Users\g13s0714\Desktop\CS Honours\GitHub\JavaIPC\Implementation"
javac WindowsIPC.java
javah -jni -classpath "C:\Users\g13s0714\Desktop\CS Honours\GitHub\JavaIPC\Implementation" WindowsIPC
