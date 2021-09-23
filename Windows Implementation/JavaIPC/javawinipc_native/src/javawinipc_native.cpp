#include "../headers/windowsipc_BaseIpc.h"

JNIEXPORT jstring JNICALL Java_windowsipc_BaseIpc_ping
  (JNIEnv *jvm, jobject obj, jstring pong) {
	return pong;
}
