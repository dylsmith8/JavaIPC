/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class windowsipc_AnonymousPipe */

#ifndef _Included_windowsipc_AnonymousPipe
#define _Included_windowsipc_AnonymousPipe
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     windowsipc_AnonymousPipe
 * Method:    create
 * Signature: (I)Lwindowsipc/Pipe;
 */
JNIEXPORT jobject JNICALL Java_windowsipc_AnonymousPipe_create
  (JNIEnv *, jobject, jint);

/*
 * Class:     windowsipc_AnonymousPipe
 * Method:    write
 * Signature: (J[B)Z
 */
JNIEXPORT jboolean JNICALL Java_windowsipc_AnonymousPipe_write
  (JNIEnv *, jobject, jlong, jbyteArray);

/*
 * Class:     windowsipc_AnonymousPipe
 * Method:    read
 * Signature: (JI)[B
 */
JNIEXPORT jbyteArray JNICALL Java_windowsipc_AnonymousPipe_read
  (JNIEnv *, jobject, jlong, jint);

/*
 * Class:     windowsipc_AnonymousPipe
 * Method:    closeHandle
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_windowsipc_AnonymousPipe_closeHandle
  (JNIEnv *, jobject, jlong);

/*
 * Class:     windowsipc_AnonymousPipe
 * Method:    peek
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_windowsipc_AnonymousPipe_peek
  (JNIEnv *, jobject, jlong, jint);

#ifdef __cplusplus
}
#endif
#endif
