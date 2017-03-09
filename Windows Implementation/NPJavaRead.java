/*
  Author: Dylan Smith
  Date: 10 September 2016

  NOTE: delibrately will fail
*/
import java.io.*;
import java.util.Scanner;
public class NPJavaRead {
  public static void main (String[] args) {

    final String PIPE_NAME = "\\\\.\\Pipe\\JavaPipe";
    WindowsIPC winIPC = new WindowsIPC();

    int pipeHandle = winIPC.createNamedPipeServer(PIPE_NAME);

    Thread t = new Thread(new NamedPipeThread(pipeHandle));
    t.start();

    byte [] messageReceived = winIPC.getMessageFromServerEndOfNamedPipe(pipeHandle);  
    
    System.out.println(messageReceived.length);
    //for (int i = 0; i < messageReceived.length ; i++) {
      //System.out.println(messageReceived[i]);
    //}
  }

private static class NamedPipeThread implements Runnable {

    private long executionTime = 0;
    private final int pipeHandle;
    WindowsIPC winIPC = new WindowsIPC();

    public NamedPipeThread (int pipeHandle) {
      this.pipeHandle = pipeHandle;
    } // constructor

    public void run () {

			// creates a client and deposits a message into the slot
      byte [] data = initTestData(40);

      long time = System.nanoTime();
      int sendMessageResult = winIPC.writeMessageToNamedPipeServer(pipeHandle, data);
      executionTime = System.nanoTime() - time;
     
      if (sendMessageResult != -1) {
        System.out.println("An error occured writing the data to the named pipe");
        return;
      }
      else {
        System.out.println("data wrote successfully");

        // now fetch the data
      }

      executionTime = System.nanoTime() - time;
	    	System.out.println("Time to write data: " + executionTime);
    
    } // run

    private byte[] initTestData(int arraySize) {
      byte [] data = new byte[arraySize];

      for (int i = 0; i < data.length; i++) data[i] = 0x02;

      return data;
    }
  } // inner class FIFOThread
}
