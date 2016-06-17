#### Named Pipes

* Refs
  - http://www.codeproject.com/Articles/13724/Windows-IPC#_comments
  - http://stackoverflow.com/questions/14306499/non-blocking-connectnamedpipe-event-not-getting-signaled
  - https://www.google.co.za/url?sa=t&rct=j&q=&esrc=s&source=web&cd=2&ved=0ahUKEwi-jOSpma_NAhUrLMAKHUKWBfQQFgghMAE&url=https%3A%2F%2Fmsdn.microsoft.com%2Fen-us%2Flibrary%2Fwindows%2Fdesktop%2Faa365146(v%3Dvs.85).aspx&usg=AFQjCNFshK5YWVg8eVDlWEXjEu7gUAO3ug&sig2=XVzunYCDfNwKSMmaHjU24A&bvm=bv.124272578,d.d24&cad=rja

Named pipes were implemented by creating the server process (`createNamedPipeServer()`) first. The message the server accepts is stored in a char buffer that is 1K. The Windows API function `CreateNamedPipe` is called that specifies a handle to the pipe. The `NO_WAIT` flag indicates an asynchronous IO which means that `ConnectNamedPipe()` returns immediately - this means that the thread is not blocked waiting for a client process to connect to it. The pipe is specified to allow read and writes and accept a byte stream as messages to be sent. It was initially tested by giving the client a hardcoded string value that it sent to the server process. Error checking is in place to ensure that the server process was created properly.

The client native method was then implemented. The method accepts a string message as an argument which is then sent to the server process by getting a handle to it and using the pipes name (which is specified when the server was created). It calls `WriteFile` which allows a read and write and opens the existing named pipe (i.e. the server), it then ensures that the message was sent to the server correctly
