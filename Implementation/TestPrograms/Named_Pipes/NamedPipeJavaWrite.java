/*
  Author: Dylan Smith
  Date: 10 September 2016
*/
import java.io.*;
public class NamedPipeJavaWrite {
    public static void main (String [] args) {
        final String PIPE_NAME = "\\\\.\\Pipe\\JavaPipe";
        WindowsIPC winIPC = new WindowsIPC();
        try {
            // creates a client and deposits a message into the slot
            byte [] data = new byte[40000];
            for (int i = 0; i < data.length; i++) data[i] = 0x02;
            FileOutputStream out = new FileOutputStream(PIPE_NAME);
            long time = System.nanoTime();
            out.write(data);
            long y = ((System.nanoTime() - time));
            System.out.println("Time to send message: "+ y + "ns");
            System.out.println("Wrote to named pipe ok");
         }
        catch (IOException exc) {
            System.err.println("I/O Error: " + exc);
            exc.printStackTrace();
        }
    }
}
