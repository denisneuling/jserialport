#include "JavaException.h"

AbstractJavaException::AbstractJavaException(char *message) : std::runtime_error(message), message(message){
};

const char* AbstractJavaException::getMessage() {
   return const_cast<char*>(message);
};

jint AbstractJavaException::delegate(JNIEnv *env){
    jclass exClass;
    exClass = env->FindClass(this->className);
    return env->ThrowNew(exClass, this->getMessage());
};

JavaRuntimeException::JavaRuntimeException(char *message) : AbstractJavaException(message) {
    this->className = "java/lang/RuntimeException";
};

JavaIOException::JavaIOException(char *message) : AbstractJavaException(message) {
    this->className = "java/io/IOException";
};