#include "../../headers/windowsipc_Mailslot.h"
#include "../../headers/utilities.h"
#include "windows.h"
#include <jni.h>
#include <stdio.h>
#include <errno.h>
#include <string>
#include <cstdio>

bool WriteSlot(HANDLE slotHandle, jbyte *str, long length) {
    DWORD bytesWritten;

    bool result = WriteFile(
        slotHandle,
        str,
        length,
        &bytesWritten,
        NULL
    );

    return result;
}

JNIEXPORT jlong JNICALL Java_windowsipc_Mailslot_create
  (JNIEnv *env, jobject obj, jstring name, jint bufferSize) {
    const char* nameMailslot = (env)->GetStringUTFChars(name, NULL);

    if (nameMailslot == NULL)
        Throw(env, "Could find a name for the mailslot");

    HANDLE mailslotHandle;

    mailslotHandle = CreateMailslot (
        nameMailslot,
        bufferSize,
        MAILSLOT_WAIT_FOREVER,
        NULL
    );

    (env)->ReleaseStringUTFChars(name, nameMailslot);

    if (mailslotHandle == NULL)
        Throw(env, "Could not create mailslot");

    // this makes me nervous...precision issues?
    return (jlong)mailslotHandle;
}

JNIEXPORT void JNICALL Java_windowsipc_Mailslot_write
  (JNIEnv *env, jobject obj, jstring name, jbyteArray data) {
    const char* nameMailslot = (env)->GetStringUTFChars(name, NULL);

    HANDLE hFile = CreateFile(
        nameMailslot,
        GENERIC_WRITE,
        FILE_SHARE_READ,
        (LPSECURITY_ATTRIBUTES) NULL,
        OPEN_EXISTING,
        FILE_ATTRIBUTE_NORMAL,
        (HANDLE) NULL
     );

    if (hFile == INVALID_HANDLE_VALUE) {
        (env)->ReleaseStringUTFChars(name, nameMailslot);
        Throw(env, "Could not create write handle");
    }

    (env)->ReleaseStringUTFChars(name, nameMailslot);

    jbyte *str = (env)->GetByteArrayElements(data, NULL);

    bool written = WriteSlot(hFile, str, (env)->GetArrayLength(data));
    if (!written) {
        (env)->ReleaseByteArrayElements(data, str, JNI_ABORT);
        CloseHandle(hFile);
        Throw(env,"Failed to write data to the mailslot");
    }

    CloseHandle(hFile);
    (env)->ReleaseByteArrayElements(data, str, JNI_ABORT);
}

JNIEXPORT jbyteArray JNICALL Java_windowsipc_Mailslot_read
  (JNIEnv *env, jobject obj, jstring name, jlong handle, jint bufferSize) {
    HANDLE slothandle = (HANDLE)handle;

    jbyte buffer[bufferSize];
    DWORD cbBytes, infoMessage, numMessages;
    jboolean result;

    bool mailslotInfo = GetMailslotInfo (
        slothandle,
        NULL,
        &infoMessage,
        &numMessages,
        NULL
    );

    if (!mailslotInfo)
        Throw(env, "Failed to retrieve mailslot info before read attempt");

    if (infoMessage == MAILSLOT_NO_MESSAGE)
        return NULL;

    if (numMessages > 0) {
        result = ReadFile (
            slothandle,
            buffer,
            bufferSize,
            &cbBytes,
            NULL
        );

        if (!result || cbBytes == 0)
            Throw(env, "Failed to read data from mailslot");

        jbyteArray readData = (env)->NewByteArray(sizeof(cbBytes));
        (env)->SetByteArrayRegion(readData, 0, sizeof(cbBytes), buffer);

        return readData;
    }

    return NULL;
}

JNIEXPORT void JNICALL Java_windowsipc_Mailslot_remove
  (JNIEnv *env, jobject obj, jlong handle) {
    HANDLE slotHandle = (HANDLE)handle;

    if (slotHandle == INVALID_HANDLE_VALUE)
            return;

    if (!CloseHandle(slotHandle))
        Throw(env, "Failed to close mailslot handle");
}
