/*
    Author: Dylan Smith
    Date: 31 August 2016

    NOTE: WRITES TO MAILSLOT WITHOUT USING JNI
*/
import java.io.*;
public class WriteToMailslot {
    public static void main (String [] args) {
        try {
            final String MAILSLOT_NAME = "\\\\.\\mailslot\\javaMailslot";
            WindowsIPC winIPC = new WindowsIPC();
            FileOutputStream out = new FileOutputStream(MAILSLOT_NAME);
            byte [] data = new byte[40000];
            for (int i = 0; i < data.length; i++) data[i] = 0x02;
            long time = System.nanoTime();
            out.write(data);
            long y = ((System.nanoTime() - time));

            System.out.println("Time to send message: "+ y + "ns");
        }
        catch (IOException exc) {
            System.err.println("I/O Error: " + exc);
            exc.printStackTrace();
        }

    }
}
