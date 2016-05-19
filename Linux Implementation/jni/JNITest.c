#include <stdio.h>
#include <jni.h>
#include "JNITest.h"

/*
 * Class:     JNITest
 * Method:    whatNow
 * Signature: ([B)V
 */
JNIEXPORT void JNICALL Java_JNITest_whatNow (JNIEnv *env, jobject obj, jbyteArray b)
  { printf("How about some COFFEE!!!!\n");
    int len = (*env)->GetArrayLength(env, b);
    printf("Array has %i elements\n", len);
    jbyte* bytes = (*env)->GetByteArrayElements(env, b, 0);
    int k;
    for (k = 0; k < len; k++)
      { printf("%i ", bytes[k]);
      }
    printf("\n");
    return;
  } /* whatNow */
  
  
/* To compile:
   gcc -o libJNITest.so -shared -I/usr/lib/jvm/java-6-sun/include -I/usr/lib/jvm/java-6-sun/include/linux JNITest.c
*/
