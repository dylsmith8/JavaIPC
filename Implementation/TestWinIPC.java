import java.io.*;
import java.util.Scanner;
public class TestWinIPC {
  public static void main (String[] args)
    {

      WindowsIPC winIPC = new WindowsIPC();

      // TEST NAMED PIPES
      final String pipeName = "\\\\.\\Pipe\\JavaPipe";

      if (winIPC.createNamedPipeServer(pipeName) == 0) {
        System.out.println("named pipe creation succeeded");
        Thread t = new Thread(new NamedPipeThread(pipeName));
        t.start();
        try {
          System.out.println("opening pipe for input");
          BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(pipeName)));
          System.out.println("waiting to read");
          String line = br.readLine();
          System.out.println("Read from pipe OK: " + line);
          br.close();

          Scanner sc = new Scanner (System.in);
          String x = sc.nextLine();

          winIPC.closeNamedPipe();
        }
        catch (IOException exc) {
          System.err.println("I/O Error: " + exc);
          exc.printStackTrace();
        }

      }
} //main

private static class NamedPipeThread implements Runnable {
    private String pipeName;

    public NamedPipeThread (String pipeName) {
      this.pipeName = pipeName;
    } // constructor

    public void run () {
     try {
         PrintWriter pw = new PrintWriter(new FileOutputStream(pipeName));
         pw.println("Hello Pipe");
         System.out.println("Wrote to named pipe OK");
         pw.close();
      }
      catch (IOException exc) {
          System.err.println("I/O Error: " + exc);
          exc.printStackTrace();
      }
    } // run
  } // inner class FIFOThread
}
