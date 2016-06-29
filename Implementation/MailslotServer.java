/*
  Author: Dylan Smith
  Date: 29 June 2016

  Simple class to test mailslots
*/

public class MailslotServer {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();

    String message = winIPC.createMailslot("\\\\.\\mailslot\\javaMailslot");
    System.out.println("Message received in Java:" + message);
  } // main
} // class mailslot server
