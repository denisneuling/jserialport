#include "JavaConverter.h"

#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <vector>
#include <string>

jobject JavaConverter::ArrayList_from_Vector_chars(JNIEnv *env, std::vector<char*> v){
    jclass clazz = (*env).FindClass("java/util/ArrayList");
    jobject list = (*env).NewObject(clazz, (*env).GetMethodID(clazz, "<init>", "()V"));

    for(unsigned int i=0; i<v.size(); i++){
        char* str = (char*) static_cast<char*>(v[i]);
        jstring javaString = (*env).NewStringUTF(str);
        (*env).CallBooleanMethod(list, (*env).GetMethodID(clazz, "add", "(Ljava/lang/Object;)Z"), javaString);
    }

    return list;
};

jobject JavaConverter::ArrayList_from_Vector_strings(JNIEnv *env, std::vector<std::string> v){
    jclass clazz = (*env).FindClass("java/util/ArrayList");
    jobject list = (*env).NewObject(clazz, (*env).GetMethodID(clazz, "<init>", "()V"));

    for(unsigned int i=0; i<v.size(); i++){
        const char* str = (char*) static_cast<const char*>(v[i].c_str());
        jstring javaString = (*env).NewStringUTF(str);
        (*env).CallBooleanMethod(list, (*env).GetMethodID(clazz, "add", "(Ljava/lang/Object;)Z"), javaString);
    }

    return list;
};