/**   This class creates the Windows IPC functions using a JNI shared library
  *   Author: Dylan Smith
  *   Start Date: 5 May 2016
  *   Last Modified: 16 October 2017
  *   Follows Linux style of error reporting
  *     -1 indicates failure. 0 indicates success.
  */

import java.io.*;
import java.net.*;

public class WindowsIPC {

    public native byte[] createMailslot(String name);

    public native int connectToMailslot(byte[] message);

    public native int createAnonPipe(byte[] message);

    public native byte[] getAnonPipeMessage(int pipeHandle);

    public native int createNamedPipeServer(String pipeName);

    public native byte[] getMessageFromServerEndOfNamedPipe(int handle);

    public native int writeMessageToNamedPipeServer(int pipeHandle, byte [] message);

    public native int createNamedPipeClient(byte[] message);

    public native byte[] openWinsock();

    public native int createWinsockClient(byte[] message);

    public byte[] createJavaSocketServer(int port) {
    byte[] messageRcv = null;
    try {
        ServerSocket serverSocket = new ServerSocket(port);
        Socket server = serverSocket.accept();
        DataInputStream in = new DataInputStream(server.getInputStream());

        int length = in.readInt();
        messageRcv = new byte[length];

        if (length > 0) in.readFully(messageRcv, 0, messageRcv.length);

        server.close();
    } catch(IOException e) {
        e.printStackTrace();
        }
    return messageRcv;
    }


    public int createJavaSocketClient(String host, int port, byte[] message) {
    try {
        Socket client = new Socket(host, port);

        OutputStream outToServer = client.getOutputStream();
        DataOutputStream out = new DataOutputStream(outToServer);
        out.writeInt(message.length);
        out.write(message);

        client.close();

    } catch (IOException e) {
        e.printStackTrace();
        return -1;
    }
    return 0;
    }

    public native int createFileMapping (byte[] message);

    public native byte[] openFileMapping();

    public native String createDataCopyWindow();

    public native int sendDataCopyMessage (byte[] message);

    public native int createSemaphore(String name, int initCount, int maxCount);

    public native int openSemaphore(String semName);

    public native int waitForSingleObject(int handle);

    public native int releaseSemaphore(int handle, int incValue);

    static {
    System.loadLibrary("WindowsIPC");
    }

    public static void main(String[] args) {
        WindowsIPC winIPC = new WindowsIPC();

        System.out.println("Windows Interprocess Communication Build Successful\n");
    }
} // class
