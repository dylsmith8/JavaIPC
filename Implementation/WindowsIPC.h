/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class WindowsIPC */

#ifndef _Included_WindowsIPC
#define _Included_WindowsIPC
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     WindowsIPC
 * Method:    createMailslot
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_WindowsIPC_createMailslot
  (JNIEnv *, jobject, jstring);

/*
 * Class:     WindowsIPC
 * Method:    connectToMailslot
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_connectToMailslot
  (JNIEnv *, jobject, jstring);

/*
 * Class:     WindowsIPC
 * Method:    createPipe
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_createPipe
  (JNIEnv *, jobject, jstring);

/*
 * Class:     WindowsIPC
 * Method:    createNamedPipeServer
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_WindowsIPC_createNamedPipeServer
  (JNIEnv *, jobject, jstring);

/*
 * Class:     WindowsIPC
 * Method:    createNamedPipeClient
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_createNamedPipeClient
  (JNIEnv *, jobject, jstring);

/*
 * Class:     WindowsIPC
 * Method:    openWinsock
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_WindowsIPC_openWinsock
  (JNIEnv *, jobject);

/*
 * Class:     WindowsIPC
 * Method:    createWinsockClient
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_createWinsockClient
  (JNIEnv *, jobject, jstring);

/*
 * Class:     WindowsIPC
 * Method:    createFileMapping
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_createFileMapping
  (JNIEnv *, jobject, jstring);

/*
 * Class:     WindowsIPC
 * Method:    openFileMapping
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_WindowsIPC_openFileMapping
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
