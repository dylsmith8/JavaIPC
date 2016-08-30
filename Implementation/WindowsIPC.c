/*
  Author: Dylan Smith
  Date Created: 19 May 2016
  Last Modified: 24 August 2016

  Implementation of native functions
*/

#ifndef WIN32_LEAN_AND_MEAN   // Header Guard
#define WIN32_LEAN_AND_MEAN     // speed up build process
#endif

#ifdef _MSC_VER
 #pragma comment(lib, "user32.lib")
#endif

#include <jni.h>  // Using JNI function
#include <stdio.h>  // C standard IO
#include <errno.h>  // Error report functions

#include "windows.h" // The Windows API
#include <winsock2.h> // Using Windows Sockets
#include <ws2tcpip.h> // Winsock 2 functions
#include <iphlpapi.h> // IP APIs for Winsock
#include <conio.h>  // Console IO

#include "WindowsIPC.h" // For native methods defined in WindowsIPC.java

#pragma comment(lib, "Ws2_32.lib") // Winsock Libray File
#pragma comment(lib, "user32.lib") // Include User Objects (for Data Copy)

#define BUFFER_SIZE 50000 // 50K buffer size (was 1024)
#define SOCKET_DEFAULT_PORT "27015" // Port number
#define LOCALHOST "127.0.0.1"
#define MEMORY_MAPPING_NAME "JavaMemoryMap" // Global shm name
#define SEMAPHORE_NAME "JavaSemaphore" // Global sem name

const jbyte *nameOfPipe; // Global variable representing the named pipe
const jbyte *nameMailslot; // Global variable reprenting the mailslot name
HANDLE pipeHandle;  // Global handle for the name pipe
jstring message;

HANDLE mailslotHandle; // global handle representing the mailslot
jbyte dcMessage;

/*
 * Class:     WindowsIPC
 * Method:    createNamedPipeServer
 * Signature: ()I
 */
JNIEXPORT jbyteArray JNICALL Java_WindowsIPC_createNamedPipeServer
  (JNIEnv * env, jobject obj, jstring pipeName) {

    jbyte buffer[BUFFER_SIZE]; // Data buffer of 50 k. This will store the data that the server receives from the client
    DWORD cbBytes; // Dytes read

    jbyteArray message; // message received from client that connects

    char error[60] = "Error";
    jstring errorForJavaProgram;
    errorForJavaProgram = (*env)->NewStringUTF(env,error);

    // Get the name of the pipe
    nameOfPipe = (*env)->GetStringUTFChars(env, pipeName, NULL);

    pipeHandle = CreateNamedPipe (
      nameOfPipe,                   // Name of the pipe
      PIPE_ACCESS_DUPLEX,           // Full Duplex Pipe
      PIPE_TYPE_MESSAGE |           // Message Stream
      PIPE_READMODE_MESSAGE |       // Read as Message Stream
      PIPE_WAIT,                    // Forces a return, so thread doesn't block
      PIPE_UNLIMITED_INSTANCES,     // 255 instances
      BUFFER_SIZE,                  // Size of read/write
      BUFFER_SIZE,
      NMPWAIT_USE_DEFAULT_WAIT,     // Timeout
      NULL                          // Default Security
    );


    if (pipeHandle == INVALID_HANDLE_VALUE)  {
      // error creating server
      printf("An Error occured creating the Named Pipe\nError Code: %d\n", GetLastError());
      return errorForJavaProgram;
    }
    else {
      printf("Server created successfully: name:%s\n", nameOfPipe);

      // waits for a client -- currently in ASYC mode so returns immediately
      jboolean clientConnected = ConnectNamedPipe(pipeHandle, NULL);

      // HANDLES THE READING OF A MESSAGE!
      // error with client connecting
      if (!clientConnected) {
        printf("An error occured with the client connection\nError Code: %d\n", GetLastError());
        CloseHandle(pipeHandle);
        return errorForJavaProgram;
      }
      else {
        jboolean resultOfPipeRead = ReadFile(
              pipeHandle, // specify pipe to read from
              buffer, // buffer to read from
              sizeof(buffer), // specify the buffer's size
              &cbBytes, // deref the bytes read
              NULL  // security
            );

        if (!resultOfPipeRead) {
          printf("An error reading from the pipe\nError Code: %d\n", GetLastError());
          CloseHandle(pipeHandle);
          return errorForJavaProgram;
        }
        else {
          CloseHandle(pipeHandle);
         (*env)->ReleaseStringUTFChars(env, pipeName, nameOfPipe);
        }
      }
    }

     message = (*env)->NewByteArray(env, (jint) cbBytes); // create message to return
     (*env)->SetByteArrayRegion(env, message, 0, (jint) cbBytes, buffer); // allocate the elements
     return message;
  }

/*
 * Class:     WindowsIPC
 * Method:    createNamedPipeClient
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_createNamedPipeClient
  (JNIEnv * env, jobject obj, jbyteArray message) {

    char buffer [BUFFER_SIZE]; // 50 K
    DWORD cbBytes;
    jsize arrLen;

    // Read the message
    jbyte *str = (*env)->GetByteArrayElements(env, message, NULL);

    if (str == NULL) {
      printf("Out of memory\n");
      return -1; // out of memory
    }
    else arrLen = (*env)->GetArrayLength(env, message);

    // assign the pipe handle
    pipeHandle = CreateFile(
        "\\\\.\\Pipe\\JavaPipe", // pipe name
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
      printf("Failed to get handle to named pipe server\nError code: %d", GetLastError());
      return -1;
    }

    // send a message
    jboolean sendMessageResult = WriteFile (
     pipeHandle,
      str, // hardcoded message to send to the client
      arrLen, // length of a message
      &cbBytes,
      NULL
    );

    // check if message write was successful
    if (!sendMessageResult || cbBytes != arrLen) {
      printf("Failed to get send message to named pipe server\nError code: %d", GetLastError());
      CloseHandle(pipeHandle);
      return -1;
    }

    // free memory allocated to the message
    (*env)->ReleaseByteArrayElements(env, message, str, JNI_ABORT);
    CloseHandle(pipeHandle);
    return 0;
  }

/*
 * Class:     WindowsIPC
 * Method:    createPipe
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_createPipe
  (JNIEnv * env, jobject obj, jstring pipeName) {
    /*
    DWORD cbBytes;
    HANDLE hAnonPipeRead;
    HANDLE hAnonPipeWrite;
    jboolean pipe;

    pipe = CreatePipe (
      hAnonPipeRead,
      hAnonPipeWrite,
      NULL,
      1024
    );

    if (!pipe) return -1;
 */
    return -1;
  } // createpipe

/*
 * Class:     WindowsIPC
 * Method:    createMailslot
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jbyteArray JNICALL Java_WindowsIPC_createMailslot
  (JNIEnv * env, jobject obj, jstring mailslotName) {

    jbyte buffer[BUFFER_SIZE]; // buffer that will store the message dumped in the slot
    DWORD cbBytes; // bytes written to the mailslot
    jboolean result; // result of read

    jbyteArray message; // message received from mailslot client

    // used to display an error if failure occurs
    char error[60] = "Error";
    jstring errorForJavaProgram;
    errorForJavaProgram = (*env)->NewStringUTF(env,error);

    nameMailslot = (*env)->GetStringUTFChars(env, mailslotName, NULL);

    mailslotHandle = CreateMailslot (
      nameMailslot,              // name
      BUFFER_SIZE,               // buffer size of 1k
      MAILSLOT_WAIT_FOREVER,     // block the thread
      NULL                       // default security
    );

    // check if mailslot was created sucessfully
    if (mailslotHandle == INVALID_HANDLE_VALUE) {
      printf("Failed to create Mailslot\nError Code: %d", GetLastError());
      return errorForJavaProgram;
    }

    // block till a connection is received
    result = ReadFile (
      mailslotHandle,   // mailslot handle
      buffer,           // buffer to put data into
      sizeof(buffer),   // size of the buffer
      &cbBytes,         // bytes in slot
      NULL              // security
    );

    if (!result || 0 == cbBytes) {
      printf("Failed to read data in Mailslot\nError Code: %d", GetLastError());
      CloseHandle(mailslotHandle);
      return errorForJavaProgram;
    }

    CloseHandle(mailslotHandle);
    message = (*env)->NewByteArray(env, (jint) cbBytes);
    (*env)->SetByteArrayRegion(env, message, 0, (jint)cbBytes, buffer);
    return message;
} //createMailslot

/*
 * Class:     WindowsIPC
 * Method:    connectToMailslot
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_connectToMailslot
  (JNIEnv * env, jobject obj, jbyteArray message) {

    DWORD cbBytes;
    jsize arrLen;
    jboolean result;  // stores the result of the WriteFile

    jbyte *str = (*env)->GetByteArrayElements(env, message, NULL);

    if (str == NULL) {
      printf("Out of memory\n");
      return -1; // out of memory
    }
    else arrLen = (*env)->GetArrayLength(env, message);

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
    if (mailslotHandle == INVALID_HANDLE_VALUE) {
      printf("Failed to get Mailslot server handle\nError Code: %d\n", GetLastError());
      return -1;
    }

    // dump a message in the mailslot
    result = WriteFile (
      mailslotHandle,   // slot handle
      str,              // to dump
      arrLen,  // size of
      &cbBytes,
      NULL
    );

    // check dump was successful
    if (!result || cbBytes != arrLen) {
      printf("Failed to dump message into the Mailslot\nErrorCode: %d\n", GetLastError());
      CloseHandle(mailslotHandle);
      return -1;
    }

    (*env)->ReleaseByteArrayElements(env, message, str, JNI_ABORT);
    CloseHandle(mailslotHandle);
    return 0;
  } // connect to mailslot

/*
 * Class:     WindowsIPC
 * Method:    openWinsock
 * Signature: ()I
 */
JNIEXPORT jbyteArray JNICALL Java_WindowsIPC_openWinsock
  (JNIEnv * env, jobject obj) {

    WSADATA wsaData; // this will contain information about the socket. Is a struct
    jbyte buffer[BUFFER_SIZE];      // buffer to store the message from the client
    int bytesReceived, bytesSent;     // number of bytes received and sent
    int bufferLen = BUFFER_SIZE;   // specify size of the buffer
    jbyteArray message;                // message to return to Java program (this is received from the client)
    int len = 0;                    // length of array to return to client

    // used to display an error if failure occurs
    char error[60] = "Error";
    jstring errorForJavaProgram;
    errorForJavaProgram = (*env)->NewStringUTF(env,error);

    int resultOfInitialisation = WSAStartup (MAKEWORD(2, 2), &wsaData); // intialise use of WS2_32.dll
    if (resultOfInitialisation != 0) {
      printf("WSAStartup failed\nError Code: %d", WSAGetLastError());
      return errorForJavaProgram;
    }

    struct addrinfo *result = NULL, *ptr = NULL, hints; //holds host address info
    ZeroMemory(&hints, sizeof (hints));
    hints.ai_family = AF_INET;                // is IPv4
    hints.ai_socktype = SOCK_STREAM;          // is a stream socket
    hints.ai_protocol = IPPROTO_TCP;          // We are using TCP
    hints.ai_flags = AI_PASSIVE;              // caller is going to use a bind function

    // Resolve the local address and port to be used by the server
    resultOfInitialisation = getaddrinfo(NULL, SOCKET_DEFAULT_PORT, &hints, &result);
    if (resultOfInitialisation != 0) {
        printf("getaddrinfo failed: %d\nError Code: %d", resultOfInitialisation, WSAGetLastError());
        WSACleanup();
        return errorForJavaProgram;
    }

    // create a socket that listens for client connections
    SOCKET ListenSocket = INVALID_SOCKET;
    ListenSocket = socket(result->ai_family, result->ai_socktype, result->ai_protocol);

    // check for some errors
    if (ListenSocket == INVALID_SOCKET) {
      printf("Error at socket(): %ld\n", WSAGetLastError());
      freeaddrinfo(result); // free address information
      WSACleanup();
      return errorForJavaProgram;
    }

    // server must BIND to a network address within the system
    resultOfInitialisation = bind(ListenSocket, result->ai_addr, (int)result->ai_addrlen);
    // check for bind errors
    if (resultOfInitialisation == SOCKET_ERROR) {
        printf("Bind failed with error: %d\n", WSAGetLastError());
        freeaddrinfo(result);
        closesocket(ListenSocket);
        WSACleanup();
        return errorForJavaProgram;
    }

    // once bound, free address info
    freeaddrinfo(result);

    // bound so now listen for a client connection
    if (listen(ListenSocket, SOMAXCONN) == SOCKET_ERROR) {
      printf("Socket listen failed with error: %d\n", WSAGetLastError());
      closesocket(ListenSocket);
      WSACleanup();
      return errorForJavaProgram;
    }

    // should now handle connection requests on the socket
    SOCKET ClientSocket = INVALID_SOCKET; // temp for accepting client connections
    ClientSocket = accept(ListenSocket, NULL, NULL);
    if (ClientSocket == INVALID_SOCKET) {
      printf("Accept failed with error: %d\n", WSAGetLastError());
      closesocket(ListenSocket);
      WSACleanup();
      return errorForJavaProgram;
    }

    // receive something until the client disconnects
    do {
      bytesReceived = recv(ClientSocket, buffer, bufferLen, 0);

      if (bytesReceived > 0) {
        printf("Bytes received: %d\n", bytesReceived);
        len = bytesReceived;
      }
      else if (bytesReceived == 0) printf("Connection closing...\n");
      else {
        printf("recv failed: %d\n", WSAGetLastError());
        closesocket(ClientSocket);
        WSACleanup();
        return errorForJavaProgram;
      }
    } while (bytesReceived > 0);

    // receiving done so shut down socket
    bytesReceived = shutdown(ClientSocket, SD_SEND);
    if (bytesReceived == SOCKET_ERROR) {
        printf("shutdown failed: %d\n", WSAGetLastError());
        closesocket(ClientSocket);
        WSACleanup();
        return errorForJavaProgram;
    }

    // cleanup
    closesocket(ClientSocket);
    WSACleanup();

    // return..
    message = (*env)->NewByteArray(env, len); // create array to return
    (*env)->SetByteArrayRegion(env, message, 0, len, buffer); // fill array with message
    return message; // return the message
  } // openWinsock

/*
 * Class:     WindowsIPC
 * Method:    createWinsockClient
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_createWinsockClient
  (JNIEnv * env, jobject obj, jbyteArray message) {

    jsize arrLen;
    WSADATA wsaData; // this will contain information about the socket. Is a struct
    struct addrinfo *result = NULL, *ptr = NULL, hints;
    SOCKET ConnectSocket = INVALID_SOCKET;    // temp socket for connection
    int bufferLen = BUFFER_SIZE;             // specify the receive buffer size
    char buffer[BUFFER_SIZE];                // receive buffer
    int iResult;

    const jbyte *str = (*env)->GetByteArrayElements(env, message, NULL); // fetch  message elems
    if (str == NULL) {
      printf("Out of memory\n");
      return -1; // out of memory
    }
    else arrLen = (*env)->GetArrayLength(env, message);

    // intialise use of WS2_32.dll
    int resultOfInitialisation = WSAStartup (MAKEWORD(2, 2), &wsaData);
    if (resultOfInitialisation != 0) {
      printf("WSAStartup failed with error: %d\n", WSAGetLastError());
      (*env)->ReleaseByteArrayElements(env, message, (jbyte*) str, JNI_ABORT);
      return -1;
    }

    ZeroMemory(&hints, sizeof(hints));
    hints.ai_family = AF_UNSPEC;     // IPv4
    hints.ai_socktype = SOCK_STREAM; // Socket Stream
    hints.ai_protocol = IPPROTO_TCP; // TCP

    // now need to resolve IP and port
    resultOfInitialisation = getaddrinfo(LOCALHOST, SOCKET_DEFAULT_PORT, &hints, &result);
    if (resultOfInitialisation != 0) {
      printf("getaddrinfo failed: %d\nError Code: %d\n", resultOfInitialisation, WSAGetLastError());
      (*env)->ReleaseByteArrayElements(env, message, (jbyte*) str, JNI_ABORT);
      WSACleanup();
      return -1;
    }

    ptr = result;

    // socket for connecting to the server
    ConnectSocket = socket(ptr->ai_family, ptr->ai_socktype, ptr->ai_protocol);

    // check socket is valid
    if (ConnectSocket == INVALID_SOCKET) {
      printf("Error at socket(): \nError Code: %d\n", WSAGetLastError());
      (*env)->ReleaseByteArrayElements(env, message, (jbyte*) str, JNI_ABORT);
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
      printf("Unable to connect to server!\nError Code: %d\n", WSAGetLastError());
      (*env)->ReleaseByteArrayElements(env, message, (jbyte*) str, JNI_ABORT);
      WSACleanup();
      return -1;
    }

    // Send an initial buffer
    iResult = send(ConnectSocket, str, arrLen, 0);
    if (iResult == SOCKET_ERROR) {
      printf("Send failed: \nError Code:%d\n", WSAGetLastError());
      (*env)->ReleaseByteArrayElements(env, message, (jbyte*) str, JNI_ABORT);
      closesocket(ConnectSocket);
      WSACleanup();
      return -1;
    }

    printf("Bytes Sent: %ld\n", iResult);

    // shutdown the connection for sending since no more data will be sent
    iResult = shutdown(ConnectSocket, SD_SEND);
    if (iResult == SOCKET_ERROR) {
      printf("shutdown failed: %d\n", WSAGetLastError());
      (*env)->ReleaseByteArrayElements(env, message, (jbyte*) str, JNI_ABORT);
      closesocket(ConnectSocket);
      WSACleanup();
      return -1;
    }

    // cleanup
    closesocket(ConnectSocket);
    WSACleanup(); // terminate use of ws2_32.dll

    // free memory allocated to the message
    (*env)->ReleaseByteArrayElements(env, message, (jbyte*) str, JNI_ABORT);

    return 0;
  } // createWinsockClient

/*
 * Class:     WindowsIPC
 * Method:    createFileMapping
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_createFileMapping
  (JNIEnv * env, jobject obj, jbyteArray message) {

    jsize arrLen;
    HANDLE mappedFileHandle;
    jbyte *buffer;
    HANDLE semaphore;

    jbyte *str = (*env)->GetByteArrayElements(env, message, NULL);
    if (str == NULL) {
      printf("Out of memory\n");
      return -1; // out of memory
    }
    else arrLen = (*env)->GetArrayLength(env, message);

    // create the semaphore here
    semaphore = CreateSemaphore(
      NULL, // default security
      1, // initial semaphore count
      1, // maximum semaphore count
      SEMAPHORE_NAME // semaphore name (is global)
    );

    // some semaphore error checking
    if (semaphore == NULL) {
      printf("Error occured creating semaphore %d\n", GetLastError());
      (*env)->ReleaseByteArrayElements(env, message, (jbyte*) str, JNI_ABORT);
      return -1;
    }

    //create mapping object
    mappedFileHandle = CreateFileMapping (
      INVALID_HANDLE_VALUE, // handle to map file. Will create later
      NULL, // default security
      PAGE_READWRITE, // allow read/write access
      0, // high order size of file mapping object
      BUFFER_SIZE, // low order size of file mapping object
      MEMORY_MAPPING_NAME // name of file mapping
    );

    if (mappedFileHandle == NULL) {
      printf("Error creating a mapped file: %d", GetLastError());
      (*env)->ReleaseByteArrayElements(env, message, (jbyte*) str, JNI_ABORT);
      return -1;
    }

    // map view of a file common address space
    buffer = MapViewOfFile (
      mappedFileHandle, // handle of newly created file mapped object
      FILE_MAP_ALL_ACCESS,  // allows access from any process
      0,
      0,
      BUFFER_SIZE // bytes to map (TEST WITH ARRLEN)
    );

    if (buffer == NULL) {
      printf("Could not map view %d", GetLastError());
      (*env)->ReleaseByteArrayElements(env, message, (jbyte*) str, JNI_ABORT);
      CloseHandle(mappedFileHandle);
      return -1;
    }

    // Buffer is mapped in memory, so copy str to buffer:
    CopyMemory(buffer, str, (arrLen * sizeof(jbyte))); // copy data into shm
    _getch(); // keep process alive

    if (UnmapViewOfFile(buffer) == 0) {
      printf("Failed to unmap view of the file copied into shm\nError Code: %d", GetLastError());
      (*env)->ReleaseByteArrayElements(env, message, (jbyte*) str, JNI_ABORT);
      CloseHandle(mappedFileHandle);
      return -1;
    }

    CloseHandle(mappedFileHandle);
    (*env)->ReleaseByteArrayElements(env, message, str, JNI_ABORT);
    return 0; // success
  }

/*
 * Class:     WindowsIPC
 * Method:    openFileMapping
 * Signature: ()I
 */
JNIEXPORT jbyteArray JNICALL Java_WindowsIPC_openFileMapping
  (JNIEnv * env, jobject obj) {

    jbyteArray message;
    HANDLE mappedFileHandle;
    jbyte *buffer;
    HANDLE semaphore;
    DWORD waitResult;

    // for errors
    char error[60] = "Error";
    jstring errorForJavaProgram;
    errorForJavaProgram = (*env)->NewStringUTF(env,error);

    // try open the semahore
    semaphore = OpenSemaphore (
      SEMAPHORE_ALL_ACCESS,
      FALSE,
      SEMAPHORE_NAME
    );

    // some error checking
    if (semaphore == NULL) {
      printf("Could not open semaphore %d\n", GetLastError());
      return errorForJavaProgram;
    }

    waitResult = WaitForSingleObject(
      semaphore,
      -1 // block
    );

    // try to open the file mapping -- START CRITICAL REGION
    // ======================================================================

    switch (waitResult) {
      case WAIT_OBJECT_0:
              mappedFileHandle = OpenFileMapping (
                FILE_MAP_ALL_ACCESS,
                FALSE,
                MEMORY_MAPPING_NAME
              );

              if (mappedFileHandle == NULL) {
                printf("Could not open file mapping");
                return errorForJavaProgram;
              }

              // read data here, must be a critical region
              buffer = MapViewOfFile(
                mappedFileHandle,
                FILE_MAP_ALL_ACCESS,
                0,
                0,
                BUFFER_SIZE
              );

              if (buffer == NULL) {
                printf("Could not map view %d", GetLastError());
                CloseHandle(mappedFileHandle);
                return errorForJavaProgram;
              }

              message = (*env)->NewByteArray(env, strlen(buffer));
              (*env)->SetByteArrayRegion(env, message, 0, strlen(buffer), buffer);

              if (!ReleaseSemaphore(semaphore, 1, NULL)) {
                printf("An error occured releasing the semaphore: %d\n", GetLastError());
                return errorForJavaProgram;
              }
              break;
        default:
          printf("Got to default \n");
    } //switch

    printf("Wait result %d\n", waitResult);
    // END CRITICAL REGION
    //=========================================================================
    // return..

    //clean up
    UnmapViewOfFile(buffer);
    CloseHandle(mappedFileHandle);

    return message; // success
  }

LRESULT WINAPI WindowsProcedure (HWND hwnd, UINT msg, WPARAM wparam, LPARAM lparam) {
  LPCSTR msgReceived;
  if (msg == WM_COPYDATA) {
    COPYDATASTRUCT* cds = (COPYDATASTRUCT*)lparam;
    if (cds->dwData) {
      msgReceived = (LPCSTR)(cds->lpData);
      return TRUE;
    }
  }
  else if (msg == WM_DESTROY) {
    PostQuitMessage(0);
    return TRUE;
  }
  return DefWindowProc(hwnd, msg, wparam, lparam);
}

/*
 * Class:     WindowsIPC
 * Method:    openDataCopy
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jstring JNICALL Java_WindowsIPC_createDataCopyWindow
  (JNIEnv * env, jobject obj) {
     char error[60] = "Error";
     jstring errorForJavaProgram;
     errorForJavaProgram = (*env)->NewStringUTF(env,error);

     WNDCLASS windowClass;
     HWND hwnd;
     MSG msg;

     memset(&windowClass, 0, sizeof(windowClass));
     windowClass.lpfnWndProc = &WindowsProcedure;
     windowClass.lpszClassName = TEXT("WindowsIPCDataCopyClass");
     windowClass.hInstance = GetModuleHandle(NULL);

     if (!RegisterClass(&windowClass)) {
       printf("failed to register class: %d\n", GetLastError());
       return errorForJavaProgram;
     }
     else {
       hwnd = CreateWindowEx(0, windowClass.lpszClassName, NULL, 0, 0, 0, 0, 0, HWND_MESSAGE, NULL, NULL, NULL);
       if (hwnd == NULL) {
         printf("Window Creation failed: %d\n", GetLastError());
         return errorForJavaProgram;
       }
       else {
         while (GetMessage (&msg, NULL, 0, 0)) {
          TranslateMessage (&msg);
          DispatchMessage (&msg);
         }
       }
     }
     jstring toReturn;
     toReturn = (*env)->NewStringUTF(env, dcMessage);
     return toReturn; // success
  }

/*
 * Class:     WindowsIPC
 * Method:    getDataCopyMessage
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_sendDataCopyMessage
  (JNIEnv * env, jobject obj, jbyteArray message) {

     jbyte *str = (*env)->GetByteArrayElements(env, message, NULL);
     jsize arrLen = (*env)->GetArrayLength(env, message);
     printf("Message size in bytes: %d\n", arrLen);

     //LPCTSTR messageString = str;
    // dcMessage = messageString;
     COPYDATASTRUCT cds;
     HWND hwnd;

     hwnd = FindWindowEx (
        HWND_MESSAGE,
        0,
        TEXT("WindowsIPCDataCopyClass"),
        0
     );

     if (hwnd == NULL) {
       printf("Couldnt find window: %d\n", GetLastError());
       return -1;
     }
     else {
       cds.dwData = 1;
       cds.cbData = arrLen;
       cds.lpData = str;
       if (!SendMessage(hwnd, WM_COPYDATA, (WPARAM)hwnd, (LPARAM)(LPVOID)&cds)) {
         printf("Couldnt send message to window. Error code: %d\n", GetLastError());
         return -1;
       }
       // message sent correctly so now destroy the window
       if (!SendMessage(hwnd, WM_DESTROY, (WPARAM)hwnd, (LPARAM)(LPVOID)&cds)) {
         printf("Couldnt send destroy message to window. Error code: %d\n", GetLastError());
         return -1;
       }
     }
     (*env)->ReleaseByteArrayElements(env, message, str, JNI_ABORT);
     return 0; // success
  }

/*
 * Class:     WindowsIPC
 * Method:    createSemaphore
 * Signature: (Ljava/lang/String;II)I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_createSemaphore
  (JNIEnv * env, jobject obj, jstring semName, jint initCount, jint maxCount) {

    HANDLE semaphore; // handle to return up to the Java world

    const jbyte *sem = (*env)->GetStringUTFChars(env, semName, NULL);
    if (sem == NULL) {
      printf("Out of memory\n");
      return -1;
    }

    if (maxCount < initCount || maxCount < 0) {
      printf("The maximum count cannot be less than the initial count and cannot be negative\n");
      return -1;
    }
    else {
      semaphore = CreateSemaphore (
        NULL, // default semaphore security
        initCount, // sem initial count
        maxCount, // sem maximum count
        sem // semahpore name
      );

      if (semaphore == NULL) {
        printf("Semaphore creation failed: \n%d", GetLastError());
        (*env)->ReleaseStringUTFChars(env, semName, sem);
        return -1;
      }
      else {
        (*env)->ReleaseStringUTFChars(env, semName, sem);
        return (jint) semaphore;
      }
    }
    (*env)->ReleaseStringUTFChars(env, semName, sem);
    return -1; // an error occured
  } // createSemaphore

/*
 * Class:     WindowsIPC
 * Method:    openSemaphore
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_openSemaphore
  (JNIEnv * env, jobject obj, jstring semName) {

    HANDLE semaphore; // handle of opened semaphore to return to Java world
    const jbyte *sem = (*env)->GetStringUTFChars(env, semName, NULL);

    semaphore = OpenSemaphore(
      SEMAPHORE_ALL_ACCESS, // allow all access
      FALSE,
      sem
    );

    if (semaphore == NULL) {
      printf("Could not open the semaphore:\n%d", GetLastError());
      (*env)->ReleaseStringUTFChars(env, semName, sem);
      return -1;
    }
    (*env)->ReleaseStringUTFChars(env, semName, sem);
    return (jint) semaphore;
  } // openSemaphore

/*
 * Class:     WindowsIPC
 * Method:    waitForSingleObject
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_waitForSingleObject
  (JNIEnv * env, jobject obj, jint semHandle) {
    DWORD waitResult;

    waitResult = WaitForSingleObject(
        (HANDLE)semHandle,
        -1 // block
     );
    if (waitResult == WAIT_OBJECT_0) return (jint) WAIT_OBJECT_0;
    else return -1;
  } //waitForSingleObejct

/*
 * Class:     WindowsIPC
 * Method:    releaseSemaphore
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_releaseSemaphore
  (JNIEnv * env, jobject obj, jint semHandle, jint incValue) {
      if (!ReleaseSemaphore((HANDLE)semHandle, incValue, NULL)) {
        printf("An error occured releasing the semaphore: %d\n", GetLastError());
        return -1;
      } else return 0;
  } // releaseSemaphore

void main() {
} // main
