#include "../../headers/windowsipc_AnonymousPipe.h"
#include "../../headers/utilities.h"
#include <stdio.h>
#include <errno.h>
#include <string>
#include <cstdio>

JNIEXPORT jobject JNICALL Java_windowsipc_AnonymousPipe_create
  (JNIEnv *env , jobject obj, jint bufferSize) {
    HANDLE hRead = INVALID_HANDLE_VALUE;
    HANDLE hWrite = INVALID_HANDLE_VALUE;

    // FindClass is quite finicky.. if my understanding is correct, it traverses the call stack from the main thread
    // and breaks if its called from any other thread because it cannot read a map of the java class IDs..
    // it also expects package names to be delimited by a '/' instead of a '.'
    jclass cls = env->FindClass("windowsipc/Pipe");
    if (cls == NULL)
        Throw(env, "Could not find Pipe class in the Java runtime");

    jmethodID ctor = env->GetMethodID(cls, "<init>", "(JJ)V");
    if (ctor == NULL)
        Throw(env, "Could not find the Pipe type's constructor");

    bool pipe = CreatePipe(
        &hRead,
        &hWrite,
        NULL,
        bufferSize
    );

    if (!pipe)
        Throw(env, "Could not create the anonymous pipe");

    if (hRead == INVALID_HANDLE_VALUE || hWrite == INVALID_HANDLE_VALUE)
        Throw(env, "Invalid read/write handles");

    jlong r = (jlong)hRead;
    jlong w = (jlong)hWrite;

    return env->NewObject(cls, ctor, r, w);
}

JNIEXPORT jboolean JNICALL Java_windowsipc_AnonymousPipe_write
  (JNIEnv *env, jobject obj, jlong handle, jbyteArray data) {
    HANDLE hWrite = (HANDLE)handle;
    if (hWrite == INVALID_HANDLE_VALUE)
        Throw(env, "Failed to write to anonymous pipe. Write handle invalid");

    jbyte *str = env->GetByteArrayElements(data, NULL);
    int len = env->GetArrayLength(data);

    DWORD bytesWritten;

    bool result = WriteFile(
        hWrite,
        str,
        len,
        &bytesWritten,
        NULL
    );

    env->ReleaseByteArrayElements(data, str, JNI_ABORT);

    if (!result || bytesWritten == 0)
        Throw(env, "Failed to write to the anonymous pipe.");

    return result;
}

JNIEXPORT jbyteArray JNICALL Java_windowsipc_AnonymousPipe_read
  (JNIEnv *env, jobject obj, jlong handle, jint bufferSize) {
    if (bufferSize < 0)
        Throw(env, "Buffer size must be a non-negative integer");

    HANDLE hRead = (HANDLE)handle;
    if (hRead == INVALID_HANDLE_VALUE)
        Throw(env, "Cannot read the anonymous pipe. Read handle invalid");

    jbyte buffer[bufferSize];
    DWORD bytesRead;

    bool result = ReadFile(
        hRead,
        buffer,
        bufferSize,
        &bytesRead,
        NULL
    );

    if (!result || bytesRead == 0)
        Throw(env, "Failed to read the anonymous pipe");

    jbyteArray readData = env->NewByteArray(sizeof(bytesRead));
    env->SetByteArrayRegion(readData, 0, sizeof(bytesRead), buffer);

    return readData;
}

JNIEXPORT jint JNICALL Java_windowsipc_AnonymousPipe_peek
  (JNIEnv *env, jobject obj, jlong handle, jint bufferSize) {
    HANDLE hPipe = (HANDLE)handle; // the read handle
    if (hPipe == INVALID_HANDLE_VALUE)
            Throw(env, "Cannot peek the anonymous pipe. Handle invalid. Ensure the read handle is passed.");

    DWORD bytesAvailable;

    // anon pipes wrap around named pipes so should be able to leverage some of its bigger brother's functions
    bool peek = PeekNamedPipe(
        hPipe,
        NULL,
        0,
        NULL,
        &bytesAvailable,
        NULL
    );

    if (!peek)
        Throw(env, "Anonymous pipe peek failed.");

    return bytesAvailable;
}

JNIEXPORT void JNICALL Java_windowsipc_AnonymousPipe_closeHandle
  (JNIEnv *env, jobject obj, jlong handle) {
    HANDLE h = (HANDLE)handle;

    if (h == INVALID_HANDLE_VALUE)
        return;

    if (!CloseHandle(h))
        Throw(env, "Failed to close handle");
}
