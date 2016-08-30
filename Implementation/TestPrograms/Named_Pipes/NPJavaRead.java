import java.io.*;
import java.util.Scanner;
public class NPJavaRead {
  public static void main (String[] args) {

    final String PIPE_NAME = "\\\\.\\Pipe\\JavaPipe";
    WindowsIPC winIPC = new WindowsIPC();

    Thread t = new Thread(new NamedPipeThread(PIPE_NAME));
    t.start();

    byte [] data = winIPC.createNamedPipeServer(PIPE_NAME);
  }

private static class NamedPipeThread implements Runnable {
    private String pipeName;
    WindowsIPC winIPC = new WindowsIPC();
    public NamedPipeThread (String pipeName) {
      this.pipeName = pipeName;
    } // constructor

    public void run () {
     try {
  			// creates a client and deposits a message into the slot
        byte [] data = new byte[40];
        long time = System.nanoTime();
 			  PrintWriter pw = new PrintWriter (new FileOutputStream (pipeName));
        pw.println(data);
        System.out.println("Time to send message: "+ ((System.nanoTime() - time))+ "ns");
 	    	System.out.println("Wrote to named pipe ok");
 	    	pw.close();
      }
      catch (IOException exc) {
          System.err.println("I/O Error: " + exc);
          exc.printStackTrace();
      }
    } // run
  } // inner class FIFOThread
}
