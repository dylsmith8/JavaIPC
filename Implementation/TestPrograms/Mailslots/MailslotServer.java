/*
  Author: Dylan Smith
  Date: 29 June 2016

  Simple class to test mailslots
*/

public class MailslotServer {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();
    byte [] data = null;
    data = winIPC.createMailslot("\\\\.\\mailslot\\javaMailslot");
    for (int i = 0; i < data.length; i++) {
        System.out.println("Message @ elem + " + i + ": " + data[i]);    
    }
  } // main
} // class mailslot server
