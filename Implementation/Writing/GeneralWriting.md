I initially had some problems implementing named pipes using Java's standard I/O streams such as 
`FileInputStream` and `FileOutputStream`. Once the named pipe was created, I tried to access the 
the pipe using the OS's file system (as can be done in Linux) using `FileOutputStream` and 
`FileInputStream` in Java. This was an attempt to limit the use of JNI calls to read and write 
data to the pipe. This created exceptions, namely a "`FileNotFoundException` All pipes are busy".
Upon further investigation, this was due to how Windows implements its piping communication mechanisms
and the various flags that are seT when the pipe was originally created --> ADD MORE DISCUSSION ON THIS!


I proceded with developing JNI methods that handle the IO. I allowed the thread to block by setting the 
`PIPE_WAIT` flag in `CreateNamedPipe`. This means that subsequent to the pipe's creation, the function will
not return until a client process connects to it. This is not ideal as it does not allow any asynchronous 
communication between the processes. The same method then handles the message received by placing it into a buffer 
that has a size of 1 KB. It then simply prints the message out. Various error checking is in place that 
ensures the pipe was created successfully and that the client connected correctly. The client was designed 
in such a way that is connects to the 'server' process and then sends a message to it. The handle to the 
pipe is global and is assigned when the server creates the pipe; the client then uses it to get the correct 
handle, then sends a message by using Windows' `WriteFile` function. The client also contains error 
checking code that ensures the handle obtained is correct and the correct number of bytes have been 
written to the pipe. In order to test this functionality, I developed two very simple Java programs:
one that created the named pipe 'server' and another that connected as a 'client' and sends a string message 
to the server. The programs called the appropirate native methods defined in the WindowsIPC class. 
