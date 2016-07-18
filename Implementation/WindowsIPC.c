/*
Author: Dylan Smith
Date Created: 19 May 2016
Last Modified: 30 June 2016

Implementation of native functions
*/

#ifndef WIN32_LEAN_AND_MEAN
#define WIN32_LEAN_AND_MEAN
#endif

#ifdef _MSC_VER
 #pragma comment(lib, "user32.lib")
#endif

#include <jni.h>
#include <stdio.h>
#include <errno.h>

#include "windows.h" // windows API
#include <winsock2.h>
#include <ws2tcpip.h>
#include <iphlpapi.h>
#include <conio.h>

#include "WindowsIPC.h" // native methods

#pragma comment(lib, "Ws2_32.lib")

#define BUFFER_SIZE 1024 // 1K buffer size
#define SOCKET_DEFAULT_PORT "27015"
#define LOCALHOST "127.0.0.1"
#define MEMORY_MAPPING_NAME "JavaMemoryMap"

const jbyte *nameOfPipe; // global variable representing the named pipe
const jbyte *nameMailslot; // global variable reprenting the mailslot name
HANDLE pipeHandle;  // global handle for the name pipe
jstring message;

HANDLE mailslotHandle; // global handle representing the mailslot

// sending application
typedef struct tagMYREC {
 char s1[80];
 char s2[80];
 DWORD n;
} MYREC;

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

    printf("named pipe message size in bytes: %d", strlen(str));

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
    printf("mailslot message size in bytes: %d", strlen(str));


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

/*
 * Class:     WindowsIPC
 * Method:    openWinsock
 * Signature: ()I
 */
JNIEXPORT jstring JNICALL Java_WindowsIPC_openWinsock
  (JNIEnv * env, jobject obj) {

    printf("Initilising Winsock Server...");

    WSADATA wsaData; // this will contain information about the socket. Is a struct
    char recvbuf[BUFFER_SIZE];      // buffer to store the message from the client
    int resultRec, iSendResult;     // number of bytes received and sent
    int recvbuflen = BUFFER_SIZE;   // specify size of the buffer
    jstring message;                // message to return to Java program (this is received from the client)

    // used to display an error if failure occurs
    char error[60] = "Error";
    jstring errorForJavaProgram;
    puts(error);
    errorForJavaProgram = (*env)->NewStringUTF(env,error);

    // intialise use of WS2_32.dll
    int resultOfInitialisation = WSAStartup (MAKEWORD(2, 2), &wsaData);
    if (resultOfInitialisation != 0) {
      printf("WSAStartup failed");
      return errorForJavaProgram;
    }

    struct addrinfo *result = NULL, *ptr = NULL, hints;
    ZeroMemory(&hints, sizeof (hints));
    hints.ai_family = AF_INET;                // is IPv4
    hints.ai_socktype = SOCK_STREAM;          // is a stream socket
    hints.ai_protocol = IPPROTO_TCP;          // We are using TCP
    hints.ai_flags = AI_PASSIVE;              // caller is going to use a bind function

    // Resolve the local address and port to be used by the server
    resultOfInitialisation = getaddrinfo(NULL, SOCKET_DEFAULT_PORT, &hints, &result);
    if (resultOfInitialisation != 0) {
        printf("getaddrinfo failed: %d\n", resultOfInitialisation);
        WSACleanup();
        return errorForJavaProgram;
    }

    // create a socket that listens for client connections
    SOCKET ListenSocket = INVALID_SOCKET;
    ListenSocket = socket(result->ai_family, result->ai_socktype, result->ai_protocol);

    // check for some errors
    if (ListenSocket == INVALID_SOCKET) {
      printf("Error at socket(): %ld\n", WSAGetLastError());
      freeaddrinfo(result);
      WSACleanup();
      return errorForJavaProgram;
    }

    // server must BIND to a network address within the system
    resultOfInitialisation = bind(ListenSocket, result->ai_addr, (int)result->ai_addrlen);
    // check for bind errors
    if (resultOfInitialisation == SOCKET_ERROR) {
        printf("bind failed with error: %d\n", WSAGetLastError());
        freeaddrinfo(result);
        closesocket(ListenSocket);
        WSACleanup();
        return errorForJavaProgram;
    }

    // once bound, free address info
    freeaddrinfo(result);

    // bound so now listen for a client connection
    if (listen(ListenSocket, SOMAXCONN) == SOCKET_ERROR) {
      printf("Listen failed with error: %ld\n", WSAGetLastError() );
      closesocket(ListenSocket);
      WSACleanup();
      return errorForJavaProgram;
    }

    // should now handle connection requests on the socket
    SOCKET ClientSocket = INVALID_SOCKET; // temp for accepting client connections
    ClientSocket = accept(ListenSocket, NULL, NULL);
    if (ClientSocket == INVALID_SOCKET) {
      printf("accept failed: %d\n", WSAGetLastError());
      closesocket(ListenSocket);
      WSACleanup();
      return errorForJavaProgram;
    }

    // receive something until the client disconnects
    do {
      resultRec = recv(ClientSocket, recvbuf, recvbuflen, 0);

      if (resultRec > 0) {
        printf("Bytes received: %d\n", resultRec);

        // Echo the buffer back to the sender
        iSendResult = send(ClientSocket, recvbuf, resultRec, 0);
        if (iSendResult == SOCKET_ERROR) {
            printf("send failed: %d\n", WSAGetLastError());
            closesocket(ClientSocket);
            WSACleanup();
            return errorForJavaProgram;
        }
        printf("Bytes sent: %d\n", iSendResult);
      }
      else if (resultRec == 0) printf("Connection closing...\n");
      else {
        printf("recv failed: %d\n", WSAGetLastError());
        closesocket(ClientSocket);
        WSACleanup();
        return errorForJavaProgram;
      }
    } while (resultRec > 0);

    // receiving done so shut down socket
    resultRec = shutdown(ClientSocket, SD_SEND);
    if (resultRec == SOCKET_ERROR) {
        printf("shutdown failed: %d\n", WSAGetLastError());
        closesocket(ClientSocket);
        WSACleanup();
        return errorForJavaProgram;
    }

    // cleanup
    closesocket(ClientSocket);
    WSACleanup();

    // return..
    puts(recvbuf);
    message = (*env)->NewStringUTF(env, recvbuf); // success
    return message;
  } // openWinsock

/*
 * Class:     WindowsIPC
 * Method:    createWinsockClient
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_createWinsockClient
  (JNIEnv * env, jobject obj, jstring message) {

    const jbyte *str = (*env)->GetStringUTFChars(env, message, NULL);

    printf("Message size in bytes: %d\n", (int) strlen(str));

    WSADATA wsaData; // this will contain information about the socket. Is a struct

    // intialise use of WS2_32.dll
    int resultOfInitialisation = WSAStartup (MAKEWORD(2, 2), &wsaData);
    if (resultOfInitialisation != 0) {
      printf("WSAStartup failed");
      return -1;
    }

    struct addrinfo *result = NULL,
                    *ptr = NULL,
                    hints;

    SOCKET ConnectSocket = INVALID_SOCKET;    // temp socket for connection
    int recvbuflen = BUFFER_SIZE;             // specify the receive buffer size
    char recvbuf[BUFFER_SIZE];                // receive buffer
    int iResult;                              // bytes received from server response

    ZeroMemory(&hints, sizeof(hints));
    hints.ai_family = AF_UNSPEC;     // IPv4 or IPv6
    hints.ai_socktype = SOCK_STREAM; // stream protocol for TCP
    hints.ai_protocol = IPPROTO_TCP; // TCP

    // now need to resolve IP and port
    resultOfInitialisation = getaddrinfo(LOCALHOST, SOCKET_DEFAULT_PORT, &hints, &result);
    if (resultOfInitialisation != 0) {
      printf("getaddrinfo failed: %d\n", resultOfInitialisation);
      WSACleanup();
      return -1;
    }

    ptr = result;

    // socket for connecting to the server
    ConnectSocket = socket(ptr->ai_family, ptr->ai_socktype, ptr->ai_protocol);

    // check socket is valid
    if (ConnectSocket == INVALID_SOCKET) {
      printf("Error at socket(): %ld\n", WSAGetLastError());
      freeaddrinfo(result);
      WSACleanup();
      return -1;
    }

    // connect to the server
    resultOfInitialisation = connect(ConnectSocket, ptr->ai_addr, (int) ptr->ai_addrlen);
    if (resultOfInitialisation == SOCKET_ERROR) {
        closesocket(ConnectSocket);
        ConnectSocket = INVALID_SOCKET;
    }

    // connected, so free some resources
    freeaddrinfo(result);

    // perform some error checking
    if (ConnectSocket == INVALID_SOCKET) {
      printf("Unable to connect to server!\n");
      WSACleanup();
      return -1;
    }

    // Send an initial buffer
    iResult = send(ConnectSocket, str, (int) strlen(str), 0);
    if (iResult == SOCKET_ERROR) {
      printf("send failed: %d\n", WSAGetLastError());
      closesocket(ConnectSocket);
      WSACleanup();
      return -1;
    }

    printf("Bytes Sent: %ld\n", iResult);

    // shutdown the connection for sending since no more data will be sent
    iResult = shutdown(ConnectSocket, SD_SEND);
    if (iResult == SOCKET_ERROR) {
      printf("shutdown failed: %d\n", WSAGetLastError());
      closesocket(ConnectSocket);
      WSACleanup();
      return -1;
    }

    // Receive data until the server closes the connection
    do {
      iResult = recv(ConnectSocket, recvbuf, recvbuflen, 0);
      if (iResult > 0)
        printf("Bytes received: %d\n", iResult);
      else if (iResult == 0)
        printf("Connection closed\n");
      else
        printf("recv failed: %d\n", WSAGetLastError());
    } while (iResult > 0);


    // shutdown the send half of the connection since no more data will be sent
    iResult = shutdown(ConnectSocket, SD_SEND);
    if (iResult == SOCKET_ERROR) {
        printf("shutdown failed: %d\n", WSAGetLastError());
        closesocket(ConnectSocket);
        WSACleanup();
        return -1;
    }

    // cleanup
    closesocket(ConnectSocket);
    WSACleanup(); // terminate use of ws2_32.dll

    // free memory allocated to the message
    (*env)->ReleaseStringUTFChars(env, message, str);

    return 0;
  } // createWinsockClient

/*
 * Class:     WindowsIPC
 * Method:    createFileMapping
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_createFileMapping
  (JNIEnv * env, jobject obj, jstring message) {

    HANDLE mappedFileHandle;
    LPCTSTR buffer;

    const jbyte *str = (*env)->GetStringUTFChars(env, message, NULL);
    printf("Mapped message size in bytes: %d", strlen(str));

    //create mapping object
    mappedFileHandle = CreateFileMapping (
      INVALID_HANDLE_VALUE,
      NULL,
      PAGE_READWRITE,
      0,
      BUFFER_SIZE,
      MEMORY_MAPPING_NAME
    );

    if (mappedFileHandle == NULL) {
      printf("Error creating a mapped file: %d", GetLastError());
      return -1;
    }

    // map view of a file into address space of a calling process
    buffer = (LPCTSTR) MapViewOfFile (
      mappedFileHandle,
      FILE_MAP_ALL_ACCESS,
      0,
      0,
      BUFFER_SIZE
    );

    if (buffer == NULL) {
      printf("Could not map view");
      CloseHandle(mappedFileHandle);
      return -1;
    }

    CopyMemory(buffer, str, (_tcslen(str) * sizeof(TCHAR))); // problem!!
     _getch(); // keeps console open for now until you press enter --> will allow the function to return

    //clean up
    UnmapViewOfFile(buffer);
    CloseHandle(mappedFileHandle);

    return 0; // success
  }

/*
 * Class:     WindowsIPC
 * Method:    openFileMapping
 * Signature: ()I
 */
JNIEXPORT jstring JNICALL Java_WindowsIPC_openFileMapping
  (JNIEnv * env, jobject obj) {

    jstring message;

    // for errors
    char error[60] = "Error";
    jstring errorForJavaProgram;
    //puts(error);
    errorForJavaProgram = (*env)->NewStringUTF(env,error);

    HANDLE mappedFileHandle;
    LPCTSTR buffer;

    mappedFileHandle = OpenFileMapping (
      FILE_MAP_ALL_ACCESS,
      FALSE,
      MEMORY_MAPPING_NAME
    );

    if (mappedFileHandle == NULL) {
      printf("Could not open file mapping");
      return errorForJavaProgram;
    }

    buffer = (LPTSTR) MapViewOfFile(
      mappedFileHandle,
      FILE_MAP_ALL_ACCESS,
      0,
      0,
      BUFFER_SIZE
    );

    if (buffer == NULL) {
      printf("Could not map view");
      CloseHandle(mappedFileHandle);
      return errorForJavaProgram;
    }

    // return..
    message = (*env)->NewStringUTF(env, buffer);

    //clean up
    UnmapViewOfFile(buffer);
    CloseHandle(mappedFileHandle);

    return message; // success
  }

/*
 * Class:     WindowsIPC
 * Method:    openDataCopy
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_openDataCopy
  (JNIEnv * env, jobject obj, jstring message) {

    HWND hwnd;
    // get the message
    const jbyte *str = (*env)->GetStringUTFChars(env, message, NULL);
    printf("Mapped message size in bytes: %d", strlen(str));

    LPCTSTR lpszString = "A message";
    COPYDATASTRUCT cds;
    cds.dwData = 1;
    cds.cbData = sizeof(TCHAR) * (_tcslen(lpszString) + 1);
    cds.lpData = lpszString;
    SendMessage(HWND_BROADCAST, WM_COPYDATA, (WPARAM)hwnd, (LPARAM)(LPVOID)&cds);
    return 0; // success
  }

/*
 * Class:     WindowsIPC
 * Method:    getDataCopyMessage
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_getDataCopyMessage
  (JNIEnv * env, jobject obj) {
    
    COPYDATASTRUCT* pcds = (COPYDATASTRUCT*)lParam;
    if (WM_COPYDATA)
    {
      LPCTSTR lpszString = (LPCTSTR)(pcds->lpData);
      printf("%s\n", lpszString);
    } else return -1;
    return 0; // success
  }


void main() {
} // main
