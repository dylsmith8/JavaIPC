/**   This class creates the Windows IPC functions using a JNI shared library
  *   Author Dylan Smith
  *   Start Date: 5 May 2016
  *   Modified: 30 June 2016
  *   Follows Linux style of error reporting
  */
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
  public native int openWinsock();

  /*
    Create a Winsock client
  */
  public native int createWinsockClient();

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
