#include "../headers/windowsipc_PingJni.h"

JNIEXPORT jstring JNICALL Java_windowsipc_PingJni_ping
  (JNIEnv *jvm, jobject obj, jstring pong) {
	return pong;
}
