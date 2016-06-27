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

const jbyte *nameOfPipe; // global variable representing the named pipe
jstring msgToReturn; 
HANDLE pipeHandle;
/*
 * Class:     WindowsIPC
 * Method:    createNamedPipeServer
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_createNamedPipeServer
  (JNIEnv * env, jobject obj, jstring pipeName) {

    jint retval = 0;
    char buffer[1024]; // data buffer of 1K. This will store the data that the server receives from the client  
    DWORD cbBytes;

    // Get the name of the pipe
    nameOfPipe = (*env)->GetStringUTFChars(env, pipeName, NULL);

    pipeHandle = CreateNamedPipe (
      nameOfPipe,                      // name of the pipe
      PIPE_ACCESS_DUPLEX,
      PIPE_TYPE_MESSAGE |
      PIPE_READMODE_MESSAGE |
      PIPE_WAIT,                    // forces a return, so thread doesn't block
      PIPE_UNLIMITED_INSTANCES,
      1024,
      1024,
      NMPWAIT_USE_DEFAULT_WAIT,
      NULL
    );

    // error creating server
    if (pipeHandle == INVALID_HANDLE_VALUE)  {
      return -1;
    }
    else {
      printf("Server created successfully: name:%s\n", nameOfPipe);
      
        // waits for a client -- currently in ASYC mode so returns immediately
      jboolean clientConnected = ConnectNamedPipe(pipeHandle, NULL);

      
      // HANDLES THE READING OF A MESSAGE!
      // error with client connecting
      if (!clientConnected) {
        return -1;
      }
      else {
        printf("Client process connected successfully\n");

        jboolean resultOfPipeRead = ReadFile(
              pipeHandle, // specify pipe to read from
              buffer, // buffer to read from
              sizeof(buffer), // specify the buffer's size
              &cbBytes, // deref the bytes
              NULL
            );

      // error read bytes
        printf("%d", resultOfPipeRead);
        
        if (!resultOfPipeRead) { 
          printf("got here");
          return -1;
        }
        else {
          printf("Message read successfully:\n");
          printf("%s\n", buffer);
          CloseHandle(pipeHandle);
        // free the memory allocated to the string
         (*env)->ReleaseStringUTFChars(env, pipeName, nameOfPipe);
        } 
      }  
    } 
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
    char buffer [1024]; // 1K
    DWORD cbBytes;

    // read the message
    const jbyte *str = (*env)->GetStringUTFChars(env, message, NULL);
    msgToReturn = message;

    // check the string
    if (str == NULL) return -1; // out of memory
    printf("Message sent to server: %s\n", str);

    // assign the pipe handle
    pipeHandle = CreateFile(
        "\\\\.\\Pipe\\JavaPipe",
        GENERIC_READ | //allows read and write access
        GENERIC_WRITE,
        0,
        NULL,
        OPEN_EXISTING, // opens the existing named pipe (define at top of file)
        0,
        NULL
    );

    // check if connected to the server process
    if (pipeHandle == INVALID_HANDLE_VALUE) {
      retval = -1;
      printf("got here");
    }
    else printf ("CreateFile successful\n");

    // send a message
    jboolean sendMessageResult = WriteFile (
     pipeHandle,
      str, // hardcoded message to send to the client 
      strlen(str) + 1, // length of a message
      &cbBytes,
      NULL
    );


    // check if message write was successful
    if (!sendMessageResult || cbBytes != strlen(str) + 1) {
      retval = -1;
      printf("got here");
    }
    else printf("write to the server successful\n");

    // free memory allocated to the message
    (*env)->ReleaseStringUTFChars(env, message, str);
    CloseHandle(pipeHandle);

    return retval;
  }

/*
 * Class:     WindowsIPC
 * Method:    closeNamedPipe
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_closeNamedPipe
  (JNIEnv * env, jobject obj) {
    jint retval = 0;
    CloseHandle(pipeHandle);
    return retval;
  }

/*
 * Class:     WindowsIPC
 * Method:    getNamedPipeMessage
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_WindowsIPC_getNamedPipeMessage
  (JNIEnv * env, jobject obj) {
     return msgToReturn;
  }
void main() {
} // main
