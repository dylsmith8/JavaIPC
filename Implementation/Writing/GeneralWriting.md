# General refs

1. http://etutorials.org/Programming/secure+programming/Chapter+9.+Networking/9.7+Performing+Interprocess+Communication+Using+Sockets/

# General Conventions

1. Java **methods** in camel case (e.g. `thisIsAJavaMethod()`)
2. Windows API/C methods in usual C-Style conventions (e.g. `ThisIsACMethod()`)

# Named Pipes

I initially had some problems implementing named pipes using Java's standard I/O streams such as
`FileInputStream` and `FileOutputStream`. Once the named pipe was created, I tried to access the
the pipe using the OS's file system (as can be done in Linux) using `FileOutputStream` and
`FileInputStream` in Java. This was an attempt to limit the use of JNI calls to read and write
data to the pipe. This created exceptions, namely a "`FileNotFoundException` All pipes are busy".
Upon further investigation, this was due to how Windows implements its piping communication mechanisms
and the various flags that are set when the pipe was originally created --> ADD MORE DISCUSSION ON THIS!


I proceeded with developing JNI methods that handle the IO. I allowed the thread to block by setting the
`PIPE_WAIT` flag in `CreateNamedPipe`. This means that subsequent to the pipe's creation, the function will
not return until a client process connects to it. This is not ideal as it does not allow any asynchronous
communication between the processes. I also set the method to return as a string so the Java program can
easily access the message that was sent to it. The same method then handles the message received by placing it into a buffer
that has a size of 1 KB. It then simply prints the message out. Various error checking is in place that
ensures the pipe was created successfully and that the client connected correctly. The client was designed
in such a way that is connects to the 'server' process and then sends a message to it. The handle to the
pipe is global and is assigned when the server creates the pipe; the client then uses it to get the correct
handle, then sends a message by using Windows' `WriteFile` function. The client also contains error
checking code that ensures the handle obtained is correct and the correct number of bytes have been
written to the pipe.


In order to test this functionality, I developed two very simple Java programs:
one that created the named pipe 'server' and another that connected as a 'client' and sends a string message
to the server. The programs called the appropriate native methods defined in the WindowsIPC class.
I included timing code to calculate the time it took for a message to be sent from one process
to another (i.e. from one Java program to the other). I ran the code ten times and collected the
timing results. The average time it took to send a message was a respectable 968873.1 ns with a byte size of 40
 ns

# Anonymous Pipes

I decided not to implement anonymous pipes due to its similarity with named pipes.
Anonymous pipes do not allow any IPC across a network (from MSDN) and are essentially a
simplified version of Named Pipes. I believe that named pipes are easier to work with
and allow a lot more flexibility. This is emphasised by the fact that Anonymous Pipes are
are not full-duplex and essentially do not allow bi-directional data exchange.  


# Mailslots

#### Refs

1. http://www.winsocketdotnetworkprogramming.com/winsock2programming/winsock2advancedmailslot14a.html

The implementation of mailslots was relatively straightforward with little issue.
This mechanism is also synchronous in nature, with the native method `createMailslot`
only returning once a client connects to the created slot. Mailslots are
also fundamentally built upon a client server model with a 'server' process creating
the slot and a client process connecting and depositing some information into the slot
which the server can then use as needed.


The method in which I implemented the mailslots was similar to that of the named pipes
implementation. `createMailslot` returns a string which is the message that the client
process deposits in the slot. The function caters for a slot with a buffer size of 1 K.
The function takes a parameter that specifies the name of the mailslot which the client
uses to connect. The `MAILSLOT_WAIT_FOREVER` flag causes the thread to block - i.e. the
function does not return until a client connects correctly. Code is included that
ensures the client connected successfully, else an error is reported when the function returns.
The Windows API function `ReadFile` checks and reads what was deposited into the slot.
The message is stored in the buffer and returns as a JNI `jstring` which the Java program
can understand. At the end of the method, the function releases resources used by JNI strings
and closes the handle to the slot.


The mailslot client was also designed in a similar fashion to that of the named pipes
client function. The function is called `connectToMailslot` and takes the message that
must be deposited into an **existing** mailslot as an argument. The message in Java is
converted into a `jbyte` and fed to the Windows API function `WriteFile`. Prior to this,
`CreateFile` is used to connect to the mailslot by returning a Windows handle to it.
This handle is then also used in the `WriteFile` operation. Some error checking code is in place
to ensure that the message was deposited correctly into the slot; if not an error is returned
(-1). At the end of the function, the handle to the mailslot is closed and the function returns.


Mailslots were tested by creating two separate Java programs representing the server and client.
Essentially two processes that wanted to communicate with the 'server' being the process
that receives data and the 'client' being the process that wants to send data (i.e.
initiate some form of communication). The server process calls the native method `createMailslot`
by invoking it using an object of the `WindowsIPC` class. The message that is receives in its
slot is stored in a simple Java string variable. The client process also uses an object of
`WindowsIPC` and invokes `connectToMailslot`, specifying an argument as a message. The message
sends correctly once the client connects successfully. The message is then received correctly
at the 'server' end and is simply printed out.


I implemented some timing code to test the efficiency of the slot. I tested mailslot
by executing them ten times and recording the average time it took to send
a message. A client averages 895244.5 ns to connect to a slot and deposit a message. This
is almost a third slower than named pipes. Almost the same results are received if you implement
a test class that makes use of a Java worker thread to connect to the mailslot server.


Once I had mailslots working correctly in terms of pure JNI calls, I proceeded to refine it
by trying out Java's standard IO mechanims (i.e. `FileInputStream` and `FileOutputStream`).
I had the intention of trying to improve the performance by limiting the use of 'jumps' to the native
code level. Mailslots make use of Windows' usual file system so I thought about accessing the
slot by simply accessing it in the same sense as a 'file'.

My code created the mailslot in the Java program's main thread with the client
created in a separate thread using Java's `Thread` class. The main thread used a WindowsIPC
object to call `createMailslot` with the mailslot name specified as a final parameter.
The client thread code was implemented in the `run` method. This created a `PrintWriter` object
that uses a FileOutputStream to write data to the mailslot using `pw.println("Some message");`
The message is then accessed by the main thread (sine `createMailslot` returns with the message
that was written to it.). By using this method, I was able to make use of slots
by simply using one JNI call to create the slot. I did not need to
call the native method `connectToMailslot` to connect to it and
send a message. In terms of performance results: a message write to the slot took 132797.1
ns which is approximately an 85% performance gain by simply limiting a single JNI call.

# Windows Sockets

#### Refs

1. https://msdn.microsoft.com/en-us/library/windows/desktop/ms737629(v=vs.85).aspx
  - Talks about configuring Winsock headers and header guards
2. https://msdn.microsoft.com/en-us/library/windows/desktop/ms738566(v=vs.85).aspx
  - Talks about Winsock initilisation
3. http://www.khambatti.com/mujtaba/ArticlesAndPapers/cse532.pdf
4. http://www.sockets.com/winsock2.htm


According to Microsoft, all processes that make use of Winsock functions must initialise
the Windows Sockets DLL before making function calls. This ensures that Winsock is supported on
the system. `WSAStartup` is used to initialise use of WS2_32.dll. This returns -1 if it fails.
This is used to handle the possibility that the system does not support socket communication.


The server was created first by making use of a default port number defined as a constant
and using the IP address of localhost (127.0.0.1). A socket is then created that
listens for any client communication. It makes use of IPv4 by using the `AF_INET` flag
and uses TCP as a communication protocol. This is then **bound** to a network address
using the `bind` function. Subsequent to a successful bind, `listen` is used for
incoming client connections. `accept` is used to handle client requests.
Once a client connection has been accepted, a do-while is used to receive the data,
do something with it, until there are no more bytes to receive from the client.
In this implementation, the server simply echoes the message back to the client,
effectively simulating a round-trip. There is some error checking in place that
ensures it fails 'gracefully' if a socket error or byte receipt error occurs.
Once this is completed, the function cleans up any resources that are used
in its implementation of the server socket.


After the server's implementation, I created the client socket.
This was designed so that when the JNI function is called, it will
connect to a previously running Winsock server that exists on localhost at
the hardcoded (#define) port number. So the programmer does not have to worry about
configuring an IP address or port number prior to a connection.
The message is a simple string that is specified as an argument with its
byte size extracted using `strlen`. The client also uses `WSAStartup` to
initialise use of WS2_32.dll. The IP address family and protocol are set
as IPv4 and TCP respectively. The function `connect` is used to connect to
the server. Once successfully connected, a do-while is used to do something until
all the server response bytes have been received. In this case it just prints
out the number of bytes received from the server which is simply the message the client sent echoed back.
Once this has been completed, resources are freed, including the socket
connections and JNI string representing the message that was sent to the server.


Testing Winsocks was done by creating two Java programs, one that creates the server and
another that connects as a client. The server program should be initiated first and the
client after. A simple message is sent from the client Java program and then
echoed back from the server to the client. This process is timed, effectively
representing the round-trip delay of the message sent. A simple string message of 40 bytes
takes roughly 1361802ns. This was calculated by dividing the round-trip time average by 2 over a period of 10 runs
The result is significantly slower than that of mailslots and named pipes so far.
A single Java program that uses a thread as a client was
also implemented that yields similar results.


# Java Sockets

This IPC mechanism does not use any native code and simply makes use of
Java's socket mechanisms. The code was written in the WindowsIPC class as simple
methods. I first implemented the socket server in the method `createJavaSocketServer`
that returns a string and takes a port number as an argument. It creates a socket server that
waits for a client connection by calling the `accept` method on a `Socket` object.
A `DataInputStream` object is used to read what a client has written to the server.
A `DataOutputStream` object is used to echo the message it received back to the client
process. Some error checking is in place to ensure that a valid port number is used.
Finally, the method returns the string value that the client sent across the socket.


After I implemented the server, I implemented the client. The client returns an int
which represents if the method executed as expected. A returned value of zero indicates that the method executed as expected.
-1 indicates an error occured. The method takes the host name, port
and message as arguments. The host name, in this case, should always be localhost (127.0.0.1).
The port number should be above 1024 to not use privileged port numbers.
A `Socket` object connects to localhost and the port number specified.
A `DataOutputStream` object writes data to the server using the method `WriteUTF` (since) a
string value is being sent from the client to the server.
The client then prints out the message that was echoed back from the server (in this case, it is
just the same message that was sent across).


Java sockets execution time and performance is significantly poor in relation to
the other IPC mechanisms that have been implemented so far. A client and server program
were implemented as well as a program that used a Java thread as a client. Both yield similar
results. After executing them 10 times and averaging the runtime for one-way sending of
a message (division by 2) the result was: 2984249,45 ns. This is significantly slower than
the other methods (even Winsock).


# File Mapping (Shared Memory)

File mapping essentially represents shared memory and as such is expected to perform
relatively well in comparison to the other IPC mechanisms that have been implemented so
far.

I went about this be declaring a native method `createFileMapping` that returns an
integer value representing the status of the method's execution. If it returns zero,
the method executed as expected, else -1 is returned. The method also takes in a string
value representing the message that is to be mapped. The native code belonging
to this method creates a memory mapped file that a process can access and read the contents of.
I then created a method called `openFileMapping` that uses native code to open an
existing memory mapped file. It also returns an integer value, with zero indicating success
and 0 indicating failure.

The `createFileMapping` native function uses a Windows `HANDLE` to create the mapped area of
memory. The Windows API function `CreateFileMapping` returns a handle to the memory mapped area.
The flag `PAGE_READWRITE` indicates a generic read and write is allowed. The function arguments
also specify the size of the region as well as a name that has been hardcoded (the programmer
therefore does not need to worry about specifying the name of the memory map). The default
Windows security parameters are specified using `NULL` as another argument. `MapViewOfFile`
is a Windows API function that essentially maps a **view** of the file into the
address space of the calling process (i.e. the `client` process that is going to access
the mapped file). To use this function, the `HANDLE` that was returned by `CreateFileMapping`
must be used along with the buffer size. Subsequent to the file's mapping,
`CopyMemory` is used to share the memory containing the message. This function also
belongs to the Windows API. The function takes the a pointer to the buffer (i.e. the location
you want to copy a piece of memory too) as well as a pointer to the actual message (i.e the source).
The size of the memory that needs to be copied must also be specified in bytes. In this case,
I used the size of the type `TCHAR` (`TCHAR` represents Unicode character strings in C++) multiplied
by the length of the message. If you mess this up, it can fatally crash the JVM - so some care
needs to be taken when doing this. `CopyMemory` does not return any value (is `void`).
`_getch` is a C++ function that is used only to keep the console open for testing purposes.
(this needs a workaround at this stage, since if you don't specify this, a calling process cannot
access the memory mapped file). Once the memory has been successfully copied, cleanup operations
take place. This is dont in the form of `UnmapViewOfFile` and `CloseHandle`.


The calling process is implemented in the form of the native function `openFileMapping`.
This function gets the message that was mapped in `createFileMapping`. It also
makes use of a Windows `HANDLE` to access the mapped file. The Windows API function
`OpenFileMapping` which takes the name of the mapping as well as all access privileges.
The buffer that stores message uses `MapViewOfFile` to retrieve the contents of what
was mapped, using the handle that was returned with `OpenFileMapping`.
Once the message has been put into the buffer it is converted into a jstring and returned
back to the Java code. Some cleaning up code is also used such as `UnmapViewOfFile`
and `CloseHandle`

To test this IPC mechanism, I created two Java programs: one that created the file mapping
and another that opened it as such. The first program simply calls the native method
`createFileMapping` with a message specified as an argument. The second program calls
`openFileMapping` and puts the return result into a variable and prints out.

Some timing code was put in to test the performance of this mechanism and
it performs significantly faster (as expected) in comparison to the other
IPC mechamisms. After ten successful timed runs, an average of 84993,3ns
was yielded. This is considerably faster than the mechanism (**work out some
% here**).

# Data Copy

#### Refs

1. https://msdn.microsoft.com/en-us/library/windows/desktop/ms632599.aspx#message_only
2. https://msdn.microsoft.com/en-us/library/windows/desktop/ms632593(v=vs.85).aspx
3. https://msdn.microsoft.com/en-us/library/windows/desktop/ms644958(v=vs.85).aspx
4. https://msdn.microsoft.com/en-us/library/windows/desktop/ms644934(v=vs.85).aspx
5. https://msdn.microsoft.com/en-us/library/windows/desktop/ms649011(v=vs.85).aspx

Data copy is an IPC mechanism that was extremely difficult to implement causing much
frustration. In order to make use of this mechanism for console applications
(as is the case with this project), a message-only window needs to be created.
This means that the window that is created is not visible to the end-user of
the system and is simply used as a means to send and receive messages and essentially
exists to dispatch messages.

In order to create the invisible window, the programmer has to register a 'class'
of it prior to calling the code that actually creates it. This is done in the
native method `createDataCopyWindow`. 'Class' in this sense
does not refer to the object-oriented concept but merely an abstraction so that
the operating system is aware of the window's existence. I made use of the struct
`WNDCLASS` that specifies the attributes of the window that needs to be registered.
In this struct, aspects such as window style, icons, cursor and background can
be specified. It is effectively used to specify the UI style. For the purposes of this
research, I specified the name of the class, the handle to the instance that contains the
window procedure (in this case a reference to my own function). I then used `RegisterClass`,
passing the struct as an argument. Since this project is only developing IPC for
Java console applications, it is not necessary to specify a window style.  

Since windows identifies all forms belonging to applications by making use of
handles, I assigned a newly created window using (`CreateWindowEx`) that takes
the class name and the flag `HWND_MESSAGE` that forces it to be a message-only
window. I simply left the other parameters such as the width, height, style etc
of the window as zero and null as needed.

Once the window has successfully been created, you need to implement a message loop
so the system will continually poll whether a message has been received or not.
This was done using the function `GetMessage` in a while loop header using a `MSG` value which consists
of message data from the window's message queue. In the loop, a call to `TranslateMessage` which
posts the message to the window's message queue. In addition, a call to `DispatchMessage`
takes place. This function dispatches the message to my own windows procedure function
that actually handles the message as necessary.

The native method `sendsDataCopyMessage` takes a string message and passes it into the JNI.
Here it is assigned to `dwData` (a pointer to the data) of the `COPYDATASTRUCT` which is a struct that contains data
to be sent to another application/process. The size in bytes of the data also needs to be specified within
the struct by setting the property `cbData`. Once this has been done, `SendMessage` can be called
that takes a handle to the newly-created message only window, the `WM_COPYDATA` flag
and the data struct containing the message and its related information. If this message
returns successfully, a subsequent call to `SendMessage` takes place that terminates
the message only window. This is performed by sending `WM_DESTROY`.

A windows procedure function is a programmer defined winapi function that
handles any messages received on a window (from MSDN). The function I
created called `WindowsProcedure` with signature `LRESULT WINAPI WindowsProcedure (HWND hwnd, UINT msg, WPARAM wparam, LPARAM lparam)`
handles the message received from the native method `sendDataCopyMessage`.
If the message type received was `WM_COPYDATA`, it means that some message was received
from another process and needs to be handled. This is done by extracting the
message from the `COPYDATASTRUCT` property 'lpData' using the following syntax:
`msgReceived = (LPCSTR)(cds->lpData);` where `cds` is a pointer to a `COPYDATASTRUCT`
structure. Alternatively, if the message type received was `WM_DESTROY`, this means that
window needs to be terminated. This is done by using a call to `PostQuitMessage(0)`.
This terminates the message loop and allows `createDataCopyWindow` to terminate
and return the message received. In both these cases, returning true in the
windows procedure is necessary since a message has been correctly handled.
If a message a type is not recognised and hence not handled, retuning
`DefWindowProc` takes place which allows default processing to unhandled
messages.

Data Copy appears to be an incredibly clunky way in which to
implement an IPC mechanism. From my experience of it, it is not worth implementing
it due to the fact that is only really useful in GUI-based programs. It is not
worth the trouble in terms of creating an invisible window for a console based applications
as overheads are sure to be introduced, hence code refinement on it is relatively limited.
As expected, performance is horrendous. It is even slower than sockets. An average of 10
runs resulted in a runtime of 2455624598ns or 2.46 seconds to send a simple 40 byte
string message. Not recommended.
