/*
 * File:   JavaConverter.h
 * Author: Denis Neuling (denisneuling@gmail.com)
 *
 * Created on 24. Dezember 2016, 09:01
 */

#ifndef JAVACONVERTER_H
#define JAVACONVERTER_H

#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <vector>
#include <string>

class JavaConverter {
public:
    static jobject ArrayList_from_Vector_chars(JNIEnv *env, std::vector<char*> vector);
    static jobject ArrayList_from_Vector_strings(JNIEnv *env, std::vector<std::string> vector);
};

#endif /* JAVACONVERTER_H */