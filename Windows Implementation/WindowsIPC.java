/**   This class creates the Windows IPC functions using a JNI shared library
  *   Author Dylan Smith
  *   Start Date: 5 May 2016
  *   Last Modified: 19 October 2016
  *   Follows Linux style of error reporting
  *     -1 indicates failure. 0 indicates success.
  */
import java.io.*;
import java.net.*;
public class WindowsIPC {


  /*
    Calls CreateMailslot()
    Create a Windows Mailslot. Actual C implementation returns a handle.
    Returns a byte array representing what was dumped in the mailslot by the client
  */
  public native byte[] createMailslot(String name);

  /*
    Native method connects to an exisiting mailslot
    Created by a previous call of createMailslot
  */
  public native int connectToMailslot(byte[] message);

  /*
    Calls CreatePipe()
    Creates an anonymous pipe between processes.
    Return -1 if failed, 1 if sucessful
  */
  public native int createAnonPipe(byte[] message);

  /*
    Gets the message from the read-end of the Anonymous Pipe
  */
  public native byte[] getAnonPipeMessage(int pipeHandle);

  /*
    Calls CreateNamedPipe()
    Create a server Named Pipe
    Will wait for a client process to connect and send a message
    Returns a byte array representing the client's message
  */
  public native byte[] createNamedPipeServer(String pipeName);

  /*
    Creates a client that will connect to an existing named pipe
    and sends a message as a byte array
  */
  public native int createNamedPipeClient(byte[] message);

  /*
    Creates a Windows Sockets 2 Socket Connection
    Acts as the server process
  */
  public native byte[] openWinsock();

  /*
    Create a Winsock client
    Connects to an existing Winsock Server
  */
  public native int createWinsockClient(byte[] message);

  /*
    Create a Java Sockets Server (no JNI) on local host at specific port
    Returns the message as a byte array
  */
  public byte[] createJavaSocketServer(int port) {
    byte[] messageRcv = null;
    try {
        ServerSocket serverSocket = new ServerSocket(port);
        Socket server = serverSocket.accept();
        DataInputStream in = new DataInputStream(server.getInputStream());

        int length = in.readInt();
        messageRcv = new byte[length];

        if (length > 0) in.readFully(messageRcv, 0, messageRcv.length);

        server.close();
    } catch(IOException e) {
        e.printStackTrace();
      }
    return messageRcv; // return the message sent from the client
  }

  /*
    Create a Java socket client that connects to a Java socket server (should be called after createJavaSocketServer)
  */
  public int createJavaSocketClient(String host, int port, byte[] message) {
    try {
      Socket client = new Socket(host, port);

      OutputStream outToServer = client.getOutputStream();
      DataOutputStream out = new DataOutputStream(outToServer);
      out.writeInt(message.length);
      out.write(message);

      client.close();

    } catch (IOException e) {
      e.printStackTrace();
      return -1; // error occured
    }
    return 0; // success
  }

  /*
    Native method that creates a shared memory mapped file
    It maps message into shared memory
  */
  public native int createFileMapping (byte[] message);

  /*
    Open an existing file mapping
    Attempt to read the data from shared memory and return it in the form
    of a byte array
  */
  public native byte[] openFileMapping();

  /*
    Create a message-only window that a message can be sent to
    Returns the message sent to the window by the client
  */
  public native String createDataCopyWindow();

  /*
    Use SendMessage to send a data copy message to an exisiting message-only
    window
  */
  public native int sendDataCopyMessage (byte[] message);

   /*
    Create a Windows Semaphore
    The name, an intial count and maximum count are specified as arguments
    Returns a handle to the newly created semaphore
  */
  public native int createSemaphore(String name, int initCount, int maxCount);

  /*
    Attempt to open the semaphore called "semName"
    This returns a handle to the semaphore specified by semName
  */
  public native int openSemaphore(String semName);

  /*
    Attempt to access the semaphore object
    Wait for the semaphore to become signalled
    Returns 0 (for WAIT_RESULT0) if the semaphore is
    signalled, else a non-zero value is returned if blocked
  */
  public native int waitForSingleObject(int handle);

  /*
    Release the semaphore object and increment its count
  */
  public native int releaseSemaphore(int handle, int incValue);

  /*
    Load the native library (WindowsIPC.dll)
    Not omission of .dll when calling the library
  */
  static {
    System.loadLibrary("WindowsIPC");
  }

  /*
  Main method.
  */
  public static void main(String[] args) {
      WindowsIPC winIPC = new WindowsIPC();

      System.out.println("Windows Interprocess Communication Build Successful\n");
  }
} // class
