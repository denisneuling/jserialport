#include <jni.h>

#include "com_metapatrol_jserialport_SerialPort.h"
#include "com_metapatrol_jserialport_SerialPort_callback.h"
#include "SerialPort.h"
#include "JavaException.h"
#include "JavaConverter.h"
#include <string.h>

#ifdef __cplusplus
extern "C" {
#endif

const char *UNKNOWN_ERROR = "Unknown error";

JNIEXPORT jobject JNICALL Java_com_metapatrol_jserialport_SerialPort_nativeAvailablePorts(JNIEnv *env, jclass clazz){
    std::vector<std::string> vec = SerialPort::GetAvailablePortNames();
    return JavaConverter::ArrayList_from_Vector_strings(env, vec);
}

JNIEXPORT jobject JNICALL Java_com_metapatrol_jserialport_SerialPort_nativeConnect(JNIEnv *env, jobject instance, jstring port, jint baudIn, jint baudOut){
    const char *portName = env->GetStringUTFChars(port, JNI_FALSE);
    SerialPort *connection = new SerialPort(portName, baudIn, baudOut);
    try{
        connection->Connect();
    } catch (AbstractJavaException& e){
        delete connection;

        e.delegate(env);
    } catch (const std::exception& e){
        delete connection;

        throwJavaRuntimeExceptionDirect(env, UNKNOWN_ERROR);
    }
    env->ReleaseStringUTFChars(port, portName);
    jobject pointer = env->NewDirectByteBuffer((void*) connection, sizeof(SerialPort));
    return pointer;
}

JNIEXPORT void JNICALL Java_com_metapatrol_jserialport_SerialPort_nativeDisconnect(JNIEnv *env, jobject instance, jobject pointer){
    SerialPort *connection = (SerialPort*) env->GetDirectBufferAddress(pointer);
    try{
        connection->Disconnect();
    }catch(AbstractJavaException& e){
        delete connection;

        e.delegate(env);
    } catch (const std::exception& e){
        delete connection;

        throwJavaRuntimeExceptionDirect(env, UNKNOWN_ERROR);
    }
    delete connection;
}

JNIEXPORT jint JNICALL Java_com_metapatrol_jserialport_SerialPort_nativeWrite(JNIEnv *env, jobject instance, jobject pointer, jstring data){
    SerialPort *connection = (SerialPort*) env->GetDirectBufferAddress(pointer);
    const char *nativeString = env->GetStringUTFChars(data, JNI_FALSE);
    jint written = -1;
    try{
        char* strNativeString = const_cast<char*>(nativeString);
        connection->Write(strNativeString);
        written = strlen(strNativeString);
    }catch(AbstractJavaException& e){
        delete connection;
        env->ReleaseStringUTFChars(data, nativeString);
        e.delegate(env);
    } catch (const std::exception& e){
        delete connection;
        env->ReleaseStringUTFChars(data, nativeString);
        throwJavaRuntimeExceptionDirect(env, UNKNOWN_ERROR);
    }
    env->ReleaseStringUTFChars(data, nativeString);
    return written;
}

JNIEXPORT jstring JNICALL Java_com_metapatrol_jserialport_SerialPort_nativeRead(JNIEnv *env, jobject instance, jobject pointer){
    SerialPort *connection = (SerialPort*) env->GetDirectBufferAddress(pointer);

    std::string result;
    try{
        result = connection->Read();
    }catch(AbstractJavaException& e){
        delete connection;
        e.delegate(env);
    } catch (const std::exception& e){
        delete connection;
        throwJavaRuntimeExceptionDirect(env, UNKNOWN_ERROR);
    }

    return env->NewStringUTF(result.c_str());;
}

#ifdef __cplusplus
}
#endif
