/*
Author: Dylan Smith
Date Created: 19 May 2016
Last Modified: 30 June 2016

Implementation of native functions
*/

#include <jni.h>
#include <stdio.h>
#include <errno.h>

#include "windows.h" // windows API

#include "WindowsIPC.h" // native methods

#define BUFFER_SIZE 1024 // 1K buffer size

const jbyte *nameOfPipe; // global variable representing the named pipe
const jbyte *nameMailslot; // global variable reprenting the mailslot name
HANDLE pipeHandle;  // global handle for the name pipe
jstring message;

HANDLE mailslotHandle; // global handle representing the mailslot

/*
 * Class:     WindowsIPC
 * Method:    createNamedPipeServer
 * Signature: ()I
 */
JNIEXPORT jstring JNICALL Java_WindowsIPC_createNamedPipeServer
  (JNIEnv * env, jobject obj, jstring pipeName) {

    jint retval = 0;
    char buffer[BUFFER_SIZE]; // data buffer of 1K. This will store the data that the server receives from the client
    DWORD cbBytes;

    jstring message; // message received from client that connects

    char error[60] = "Error";
    jstring errorForJavaProgram;
    puts(error);
    errorForJavaProgram = (*env)->NewStringUTF(env,error);

    // Get the name of the pipe
    nameOfPipe = (*env)->GetStringUTFChars(env, pipeName, NULL);

    pipeHandle = CreateNamedPipe (
      nameOfPipe,                      // name of the pipe
      PIPE_ACCESS_DUPLEX,
      PIPE_TYPE_MESSAGE |
      PIPE_READMODE_MESSAGE |
      PIPE_WAIT,                    // forces a return, so thread doesn't block
      PIPE_UNLIMITED_INSTANCES,
      BUFFER_SIZE,
      BUFFER_SIZE,
      NMPWAIT_USE_DEFAULT_WAIT,
      NULL
    );

    // error creating server
    if (pipeHandle == INVALID_HANDLE_VALUE)  {
      return errorForJavaProgram;
    }
    else {
      printf("Server created successfully: name:%s\n", nameOfPipe);

        // waits for a client -- currently in ASYC mode so returns immediately
      jboolean clientConnected = ConnectNamedPipe(pipeHandle, NULL);


      // HANDLES THE READING OF A MESSAGE!
      // error with client connecting
      if (!clientConnected) {
        return errorForJavaProgram;
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

        if (!resultOfPipeRead) {
          return errorForJavaProgram;
        }
        else {
          printf("Message read successfully:\n");
          CloseHandle(pipeHandle);
        // free the memory allocated to the string
         (*env)->ReleaseStringUTFChars(env, pipeName, nameOfPipe);
        }
      }
    }

   puts(buffer);
   message = (*env)->NewStringUTF(env, buffer);
   return message;
  }

/*
 * Class:     WindowsIPC
 * Method:    createNamedPipeClient
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_createNamedPipeClient
  (JNIEnv * env, jobject obj, jstring message) {

    jint retval = 0;
    char buffer [BUFFER_SIZE]; // 1K
    DWORD cbBytes;

    // read the message
    const jbyte *str = (*env)->GetStringUTFChars(env, message, NULL);

    // check the string
    if (str == NULL) return -1; // out of memory

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
    }

    // free memory allocated to the message
    (*env)->ReleaseStringUTFChars(env, message, str);
    CloseHandle(pipeHandle);

    return retval;
  }

/*
 * Class:     WindowsIPC
 * Method:    createPipe
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_createPipe
  (JNIEnv * env, jobject obj, jstring pipeName) {
    return -1;
  } // createpipe

/*
 * Class:     WindowsIPC
 * Method:    createMailslot
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jstring JNICALL Java_WindowsIPC_createMailslot
  (JNIEnv * env, jobject obj, jstring mailslotName) {

    jint retval = 0;
    char buffer[BUFFER_SIZE]; // buffer that will store the message dumped in the slot
    DWORD cbBytes; // bytes written
    jboolean result; // result of read


    jstring message; // message received from mailslot client

    // used to display an error if failure occurs
    char error[60] = "Error";
    jstring errorForJavaProgram;
    puts(error);
    errorForJavaProgram = (*env)->NewStringUTF(env,error);

    nameMailslot = (*env)->GetStringUTFChars(env, mailslotName, NULL);

    mailslotHandle = CreateMailslot (
      nameMailslot,              // name
      BUFFER_SIZE,               // buffer size of 1k
      MAILSLOT_WAIT_FOREVER,     // block the thread
      NULL                       // default security
    );

    // check if mailslot was created sucessfully
    if (mailslotHandle == INVALID_HANDLE_VALUE) return errorForJavaProgram;
    else printf("Mailslot created successfully\n");

    // block till a connection is received
    result = ReadFile (
      mailslotHandle,
      buffer,
      sizeof(buffer),
      &cbBytes,
      NULL
    );

    if (!result || 0 == cbBytes) {
      CloseHandle(mailslotHandle);
      return errorForJavaProgram;
    }
    else printf("read was successful\n");

  CloseHandle(mailslotHandle);

  puts(buffer);
  message = (*env)->NewStringUTF(env, buffer);

  return message;
} //createMailslot

/*
 * Class:     WindowsIPC
 * Method:    connectToMailslot
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_connectToMailslot
  (JNIEnv * env, jobject obj, jstring message) {

    jint retval = 0;
    //char buffer[100] = " this is a message";
    DWORD cbBytes;
    jboolean result;  // stores the result of the WriteFile

    const jbyte *str = (*env)->GetStringUTFChars(env, message, NULL);

    // connect to existing mailslot
    mailslotHandle = CreateFile (
      "\\\\.\\mailslot\\javaMailslot",   // name of mailslot
      GENERIC_WRITE,
      FILE_SHARE_READ,
      NULL,
      OPEN_EXISTING,
      FILE_ATTRIBUTE_NORMAL,
      NULL
    );

    // check if connection successful
    if (mailslotHandle == INVALID_HANDLE_VALUE) return -1;
    else printf("Connected to mailslot successfully");

    // dump a message in the mailslot
    result = WriteFile (
      mailslotHandle,
      str,
      strlen(str) + 1,
      &cbBytes,
      NULL
    );

    // check dump was successful
    if (!result || cbBytes != strlen(str) + 1) return -1;
    else printf("Dump successful\n");

    CloseHandle(mailslotHandle);
    return retval;
  } // connect to mailslot

void main() {
} // main
