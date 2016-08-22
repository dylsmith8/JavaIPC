/**   This class creates the Windows IPC functions using a JNI shared library
  *   Author Dylan Smith
  *   Start Date: 5 May 2016
  *   Modified: 30 June 2016
  *   Follows Linux style of error reporting
  */
import java.io.*;
import java.net.*;
public class WindowsIPC {


  /*
    Calls CreateMailslot()
    Create a Windows Mailslot. Actual C implementation returns a handle.
    Returns a string representing what was dumped in the mailslot by the client
    https://msdn.microsoft.com/en-us/library/windows/desktop/aa365147(v=vs.85).aspx
    http://stackoverflow.com/questions/13060626/mailslot-write-sending-same-thing-three-times-c-c
  */
  public native byte[] createMailslot(String name);

  /*
    native method connects to an exisiting mailslot
    created by a previous call of createMailslot
  */
  public native int connectToMailslot(byte[] message);


  /*
    Calls CreatePipe()
    https://msdn.microsoft.com/en-us/library/bb546102(v=vs.110).aspx
    Creates an anonymous pipe between processes.
    Return -1 if failed, 1 if sucessful
    name is the mailslots name crated by the server
  */
  public native int createPipe(String name);

  /*
    Create a server process for a named pipe
    Will wait for a client process to connect and send a message
  */
  public native byte[] createNamedPipeServer(String pipeName);


  /*
    Creates a client that will connect to an existing named pipe
    and sends a message as a string
  */
  public native int createNamedPipeClient(byte[] message);


  /*
    Initialise Winsock
  */
  public native byte[] openWinsock();

  /*
    Create a Winsock client
  */
  public native int createWinsockClient(byte[] message);

  /*
    Create a Java Sockets Server (no JNI) on local host at specific port
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
  */
  public native int createFileMapping (byte[] message);

  /*
    Open an existing file mapping
  */
  public native byte[] openFileMapping();

  /*
    Create a message-only window that a message can be sent to
  */
  public native String createDataCopyWindow();

  /*
    Use SendMessage to send a data copy message to an exisiting message-only
    window
  */
  public native int sendDataCopyMessage (byte[] message);

   /*
    Create a Windows Semaphore
  */
  public native int createSemaphore(String name, int initCount, int maxCount);

  /*
    Attempt to open the semaphore called "semName"
  */
  public native int openSemaphore(String semName);

  /*
    Attempt to access the semaphore object
  */
  public native int waitForSingleObject(int handle);

  /*
    Release the semaphore object
  */
  public native int releaseSemaphore(int handle, int incValue);

  /*
  Load the native library
  */
  static {
    System.loadLibrary("WindowsIPC");
  }

  /*
  Main method. Conduct tests here
  */
  public static void main(String[] args) {
      WindowsIPC winIPC = new WindowsIPC();

      System.out.println("Windows Interprocess Communication Build Successful\n");
  }
} // class
