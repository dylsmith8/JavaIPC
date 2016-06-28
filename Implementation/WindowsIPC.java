/**   This class creates the Windows IPC functions using a JNI shared library
  *   @author Dylan Smith
  *   Start Date: 5 May 2016
  *   Modified: 28 June 2016
  *   Follows Linux style of error reporting to simplify error reporting
  */
public class WindowsIPC {


  /*
    Calls CreateMailslot()
    Create a Windows Mailslot. Actual C implementation returns a handle.
    This function will return -1 if an error occured or 1 if the slot
    was created successfully.
    https://msdn.microsoft.com/en-us/library/windows/desktop/aa365147(v=vs.85).aspx
    http://stackoverflow.com/questions/13060626/mailslot-write-sending-same-thing-three-times-c-c
  */
  public native int createMailslot(String name);

  /*
    native method connects to an exisiting mailslot
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

      if (winIPC.createMailslot("name") == 0)
        System.out.println("Mailslot created successfully");
      else
        System.out.println("Error creating mailslot");

      System.out.println("Build Successful\n");
  }
} // class
