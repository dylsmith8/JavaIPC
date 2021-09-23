/*
  Author: Dylan Smith
  Date 30 June 2016

  Class to test mailslot connection with a Java thread
*/
import java.io.*;
public class MailslotWithThread {
  public static void main (String[] args) {

    WindowsIPC winIPC = new WindowsIPC();
    final String MAILSLOT_NAME = "\\\\.\\mailslot\\javaMailslot";

    // start the client
    Thread t = new Thread(new MailslotThread());
    t.start();
    
    byte[] x = winIPC.createMailslot(MAILSLOT_NAME);
    for (int i = 0; i < x.length; i++) System.out.println("Message received on Java side: " + x[i]);

  } // main

  private static class MailslotThread implements Runnable {
    WindowsIPC winIPC = new WindowsIPC();
    byte [] data = new byte[400];
    public void run() {
     long time = System.nanoTime();
     if (winIPC.connectToMailslot(data) == 0)
        System.out.println("Message dumped fine");
     else
        System.out.println("Message dump failed");
     System.out.println("Time to send message: "+ ((System.nanoTime() - time))+ "ns");
    } // run
  } // MailslotThread private class
} // class
