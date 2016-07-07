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
  public native String createMailslot(String name);

  /*
    native method connects to an exisiting mailslot
    created by a previous call of createMailslot
  */
  public native int connectToMailslot(String message);


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
  public native String createNamedPipeServer(String pipeName);


  /*
    Creates a client that will connect to an existing named pipe
    and sends a message as a string
  */
  public native int createNamedPipeClient(String message);


  /*
    Initialise Winsock
  */
  public native String openWinsock();

  /*
    Create a Winsock client
  */
  public native int createWinsockClient(String message);

  /*
    Create a Java Sockets Server (no JNI) on local host at specific port
  */
  public String createJavaSocketServer(int port) {
    String messageFromClient = "";
    try {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Waiting for client to connect on port " + serverSocket.getLocalPort() + "...");

        Socket server = serverSocket.accept();

        DataInputStream in = new DataInputStream(server.getInputStream());
        messageFromClient = in.readUTF();

        DataOutputStream out = new DataOutputStream(server.getOutputStream());
        out.writeUTF("Echo: " + messageFromClient);

        server.close();
    } catch(IOException e) {
        e.printStackTrace();
      }
    return messageFromClient; // return the message sent from the client
  }

  /*
    Create a Java socket client that connects to a Java socket server (should be called after createJavaSocketServer)
  */
  public int createJavaSocketClient(String host, int port, String message) {
    try {
      System.out.println("Connecting to " + host + " on port " + port);
      Socket client = new Socket(host, port);

      OutputStream outToServer = client.getOutputStream();
      DataOutputStream out = new DataOutputStream(outToServer);
      out.writeUTF(message);

      InputStream inFromServer = client.getInputStream();
      DataInputStream in = new DataInputStream(inFromServer);

      System.out.println(in.readUTF()); // print server echo
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
  public native int createFileMapping (String message);

  /*
    Open an existing file mapping
  */
  public native String openFileMapping();

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

      System.out.println("Build Successful\n");
  }
} // class
