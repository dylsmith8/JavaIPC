REM Author: Dylan Smith
REM Date: 16 October 2017

echo "Compiling test programs..."

for /r %%a in (*.java) do ( "C:\Program Files (x86)\Java\jdk1.7.0_10\bin\Javac" "%%a" -classpath "C:\Users\Dylan\Documents\GitHub\JavaIPC\Windows Implementation")

echo "Done"
pause