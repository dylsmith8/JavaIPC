# Dylan Smith 
# General Implementation Notes 
# Date Created: 17 June 2016 
# Date Modified: 19 June 2016 

#### General Writing 
The Java library is written in a file called WindowsIPC.java
with the native methods being implemented in a file called WindowsIPC.c. The C source file contains the following
header file includes: `jni.h`, `windows.h` (for access to the Windows API) and `WindowsIPC.h`. `WindowsIPC.h` is machine
generated and includes all the function prototypes for the 
methods that are declared as native in WindowsIPC.java. 

A Windows Batch Script is used to call the various commands to
build the system. It is important to note that the script refers
to locations that are specific to the computer that the library
was developed on and as such will not work on another machine. 

#### Named Pipes

* Refs
  - http://www.codeproject.com/Articles/13724/Windows-IPC#_comments
  - http://stackoverflow.com/questions/14306499/non-blocking-connectnamedpipe-event-not-getting-signaled
  - https://www.google.co.za/url?sa=t&rct=j&q=&esrc=s&source=web&cd=2&ved=0ahUKEwi-jOSpma_NAhUrLMAKHUKWBfQQFgghMAE&url=https%3A%2F%2Fmsdn.microsoft.com%2Fen-us%2Flibrary%2Fwindows%2Fdesktop%2Faa365146(v%3Dvs.85).aspx&usg=AFQjCNFshK5YWVg8eVDlWEXjEu7gUAO3ug&sig2=XVzunYCDfNwKSMmaHjU24A&bvm=bv.124272578,d.d24&cad=rja

##### NAMED PIPE SERVER 
Named pipes were implemented by declaring a native method (`createNamedPipeServer()`) first. The method was implemented using JNI and returns a value in the Linux-style indicating its success or failure. The message the server accepts is stored in a char buffer that is 1K. The Windows API function `CreateNamedPipe` is called that specifies a handle to the pipe. The `NO_WAIT` flag indicates asynchronous IO which means that a call to `ConnectNamedPipe()` returns immediately and the thread is not blocked waiting for a client process to connect to it. The pipe is specified to allow read and writes and accept a byte stream to understand messages it receives. It was initially tested by giving the client a hardcoded string value that it sent to the server process. Error checking is in place to ensure that the server process was created properly. Message reading 
and responding takes place in another method. The server does not
close the handle to the pipe before the method returns. This is 
to ensure it still exists when a client attempts to connect.
The client will close the handle when it is finished
reading/writing messages to the server.

##### NAMED PIPE CLIENT 
The named pipe client method, `createNamedPipeClient()`, implements a client to connect to an existing named pipe server. The method accepts a string message as an argument which is then sent to the server process by getting a handle to it and using the pipe's name (which is specified when the server was created).It calls `WriteFile` which allows a read and write and opens the existing named pipe (i.e. the server), it then ensures that the message was sent to the server correctly. The client is not concerned about what the server does with the message. 

The method initially hardcoded a message to send for testing 
purposes. 
