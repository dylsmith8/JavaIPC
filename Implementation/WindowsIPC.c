/*
Author: Dylan Smith
Date Created: 19 May 2016
Date Modified: 17 June 2016

Implementation of native functions
*/

#include <jni.h>
#include <stdio.h>
#include <errno.h>

#include "windows.h"

#include "WindowsIPC.h"

#define namedPipe "\\\\.\\Pipe\\JavaPipe"


/*
 * Class:     WindowsIPC
 * Method:    createNamedPipeServer
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_createNamedPipeServer
  (JNIEnv * env, jobject obj) {

    jint retval = 0;
    HANDLE pipeHandle; // handle for the named pipe
    char buffer[1024]; // data buffer of 1K
    DWORD cbBytes;

    pipeHandle = CreateNamedPipe (
      namedPipe,                      // name of the pipe
      PIPE_ACCESS_DUPLEX,
      PIPE_TYPE_MESSAGE |
      PIPE_READMODE_MESSAGE |
      PIPE_NOWAIT,                    // forces a return, so thread doesn't block
      PIPE_UNLIMITED_INSTANCES,
      1024,
      1024,
      NMPWAIT_USE_DEFAULT_WAIT,
      NULL
    );

    // error creating server
    if (pipeHandle == INVALID_HANDLE_VALUE) retval = -1;
    else printf("Server created successfully\n");

    // waits for a client -- currently in ASYC mode so returns immediately
    jboolean clientConnected = ConnectNamedPipe(pipeHandle, NULL);

    /*

    // error with client connecting
    if (!clientConnected) retval = -1;
    else printf("Client process connected successfully\n");

    jboolean resultOfPipeRead = ReadFile(
          pipeHandle, // specify pipe to read from
          buffer, // buffer to read from
          sizeof(buffer), // specify the buffer's size
          &cbBytes, // deref the bytes
          NULL
        );

    // error read bytes

    if (!resultOfPipeRead || cbBytes == 12) retval = -1;
    else printf("Message read successfully\n");

    CloseHandle(pipeHandle);
    */
    return retval;
  }

/*
 * Class:     WindowsIPC
 * Method:    createNamedPipeClient
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_createNamedPipeClient
  (JNIEnv * env, jobject obj, jstring message) {

    jint retval = 0;
    HANDLE pipeHandle;
    char buffer [1024]; // 1K
    DWORD cbBytes;

    // read the message
    const jbyte *str = (*env)->GetStringUTFChars(env, message, NULL);

    // check the string
    if (str == NULL) return -1; // out of memory
    printf("Message sent to server: %s", str);

    // assign the pipe handle
    pipeHandle = CreateFile(
        namedPipe,
        GENERIC_READ | //allows read and write access
        GENERIC_WRITE,
        0,
        NULL,
        OPEN_EXISTING, // opens the existing named pipe (define at top of file)
        0,
        NULL
    );

    // check if connected to the server process
    if (pipeHandle == INVALID_HANDLE_VALUE) retval = -1;
    else printf ("CreateFile successful\n");

    // send a message
    jboolean sendMessageResult = WriteFile (
      pipeHandle,
      str, // hardcoded message for now...
      sizeof(str), // length of a message
      &cbBytes,
      NULL
    );


    // check if message write was successful
    if (!sendMessageResult || cbBytes == 0) retval = -1;
    else printf("write to the server successful\n");

    // free memory allocated to the message
    (*env)->ReleaseStringUTFChars(env, message, str);
    CloseHandle(pipeHandle);

    return retval;
  }

void main() {
} // main
