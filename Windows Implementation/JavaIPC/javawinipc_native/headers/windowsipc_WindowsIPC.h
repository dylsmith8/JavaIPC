/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class windowsipc_WindowsIPC */

#ifndef _Included_windowsipc_WindowsIPC
#define _Included_windowsipc_WindowsIPC
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     windowsipc_WindowsIPC
 * Method:    createMailslot
 * Signature: (Ljava/lang/String;)[B
 */
JNIEXPORT jbyteArray JNICALL Java_windowsipc_WindowsIPC_createMailslot
  (JNIEnv *, jobject, jstring);

/*
 * Class:     windowsipc_WindowsIPC
 * Method:    add
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_windowsipc_WindowsIPC_add
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     windowsipc_WindowsIPC
 * Method:    subtract
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_windowsipc_WindowsIPC_subtract
  (JNIEnv *, jobject, jint, jint);

#ifdef __cplusplus
}
#endif
#endif
