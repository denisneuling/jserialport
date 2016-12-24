/*
 * File:   JavaException.h
 * Author: Denis Neuling (denisneuling@gmail.com)
 *
 * Created on 24. Dezember 2016, 09:01
 */

#ifndef JAVAEXCEPTION_H
#define JAVAEXCEPTION_H

#include <jni.h>
#include <stdexcept>

class AbstractJavaException: public std::runtime_error {
protected:
    const char *className;
    char *message;
private:
    const char* getMessage();
public:
    AbstractJavaException(char *message);
    jint delegate(JNIEnv *env);
};

class JavaRuntimeException: public AbstractJavaException {
public:
    JavaRuntimeException(char *message);
};

class JavaIOException: public AbstractJavaException {
public:
    JavaIOException(char *message);
};

static inline jint throwJavaRuntimeExceptionDirect(JNIEnv *env, const char* message) {
    return throwJavaRuntimeExceptionDirect(env, const_cast<char*>(message));
};

static inline jint throwJavaRuntimeExceptionDirect(JNIEnv *env, char* message) {
    JavaRuntimeException exception(message);
    return exception.delegate(env);
};

#endif /* JAVAEXCEPTION_H */