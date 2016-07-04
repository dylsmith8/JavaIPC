# General refs

1. http://etutorials.org/Programming/secure+programming/Chapter+9.+Networking/9.7+Performing+Interprocess+Communication+Using+Sockets/

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
