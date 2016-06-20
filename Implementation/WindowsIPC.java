/**   This class creates the Windows IPC functions using a JNI shared library
  *   @author Dylan Smith
  *   Start Date: 5 May 2016
  *   Modified: 17 June 2016
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
    Calls CreatePipe()
    https://msdn.microsoft.com/en-us/library/bb546102(v=vs.110).aspx
    Creates an anonymous pipe between processes.
    Return -1 if failed, 1 if sucessful
    name is the mailslots name crated by the server
  */
  public native int createPipe(String name);

  /*
    Create a server process for a named pipe
  */
  public native int createNamedPipeServer(String pipeName);


  /*
    Creates a Windows Named Pipe
    Calls CreateNamedPipe()
    https://msdn.microsoft.com/en-us/library/windows/desktop/aa365150(v=vs.85).aspx
    Return -1 if failed, 1 if sucessful
    name is the unique name of the named pipe
    redirects the stdout of the process concerned
  */
  public native int createNamedPipeClient(String message);

  /*
    close the handle to the named pipe created by the server
  */
  public native void closeNamedPipe();


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


      // create a named pipe server
      if (winIPC.createNamedPipeServer("\\\\.\\Pipe\\JavaPipe") != -1)
        System.out.println("Named pipe server created successfully\n");
      else
        System.out.println("Error occured creating pipe server\n");

      // create a named pipe client that sends a message
      if (winIPC.createNamedPipeClient("Hello PIPE!!") != -1)
        System.out.println("Named pipe client created successfully\n");
      else
        System.out.println("Error occured while creating client\n");

      // close the pipe handle
      /*
      if (winIPC.closeNamedPipe() != -1)
        System.out.println("Named pipe closed successfully\n");
      else
        System.out.println("Error closing named pipe\n");
      */

      System.out.println("Run Successful\n");
  }
} // class
