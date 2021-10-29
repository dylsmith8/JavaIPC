#include "../headers/utilities.h"

void Throw(JNIEnv *env, const char *error) {
    printf("Error Code: %lu\n", GetLastError());
    jclass exception = (env)->FindClass("java/lang/Exception");
    env->ThrowNew(exception, error);
}
