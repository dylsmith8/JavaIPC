#include "../headers/windowsipc_WindowsIPC.h"

JNIEXPORT jint JNICALL Java_windowsipc_WindowsIPC_add
  (JNIEnv *jvm, jobject obj, jint x, jint y) {
	return x + y;
}

JNIEXPORT jint JNICALL Java_windowsipc_WindowsIPC_subtract
  (JNIEnv *jvm, jobject obj, jint x, jint y) {
	return x - y;
}
