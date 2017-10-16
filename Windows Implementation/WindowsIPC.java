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
