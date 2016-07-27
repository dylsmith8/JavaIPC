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

    String x = winIPC.createMailslot(MAILSLOT_NAME);
    System.out.println("Message received on Java side: " + x);


  } // main

  private static class MailslotThread implements Runnable {
    WindowsIPC winIPC = new WindowsIPC();
    byte [] data = new byte[40];
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
