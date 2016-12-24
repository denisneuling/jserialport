/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * File:   SerialPort.cc
 * Author: ska
 *
 * Created on 24. Dezember 2016, 09:01
 */

//#include "SerialPort.h"
//#include "JavaException.h"
#include "../../../../src/main/cpp/SerialPort.h"
#include "../../../../src/main/cpp/JavaException.h"

#include <errno.h>
#include <fcntl.h>
#include <functional>
#include <glob.h>
#include <jni.h>
#include <sstream>
#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <string.h>
#include <termios.h>
#include <unistd.h>
#include <vector>


#ifdef __cplusplus
extern "C" {
#endif /* __cplusplus */

static const speed_t baud_table[] = {
        0, 50, 75, 110, 134, 150, 200, 300, 600, 1200, 1800, 2400, 4800,
        9600, 19200, 38400, 57600, 115200, 230400, 460800,
#ifdef __sparc__
        76800, 153600, 307200, 614400, 921600
#else
        500000, 576000, 921600, 1000000, 1152000, 1500000, 2000000,
        2500000, 3000000, 3500000, 4000000
#endif
};

static speed_t getBaud(int value){
    if(value < 0){
        return -1;
    }

    speed_t found = baud_table[0];
    int elements = sizeof(baud_table)/sizeof(baud_table[0]);
    for(int i = 0; i < elements; i++){
        speed_t availableBaud = baud_table[i];
        if(value == (signed int) availableBaud){
            return availableBaud;
        }

        found = value > (signed int) availableBaud && found < availableBaud ? availableBaud : found;
        printf("I_'%d' -> '%d'\n", i, found);
        fflush(stdout);
        /*
        if(value > (signed int) availableBaud && i+1>=elements){
            //printf("value > BAUDRATES[i] && i+1>=elements   ::: %d ::: '%d' '%d'\n", i, value, availableBaud);
            //fflush(stdout);
            return availableBaud;
        }

        if(value <= (signed int) availableBaud){
            //printf("value < BAUDRATES[i] && BAUDRATES[i]!=0 ::: %d ::: '%d' '%d'\n", i, value, availableBaud);
            //fflush(stdout);
            return availableBaud;
        }
        //printf("??????????????????????????????????????????? ::: %d ::: '%d' '%d'\n", i, value, availableBaud);
        //fflush(stdout);
        */
    }
    return found;
}

SerialPort::SerialPort(std::string portName, int baudRateIn, int baudRateOut): portName(portName) {
    this->baudRateIn = getBaud(baudRateIn);
    this->baudRateOut = getBaud(baudRateOut);
};

SerialPort::~SerialPort(){
    this->Disconnect();
};

std::vector<std::string> SerialPort::GetAvailablePortNames(){
    std::vector<std::string> result;
    glob_t glob_result;
    glob("/dev/tty*", GLOB_TILDE, NULL, &glob_result);
    for(unsigned int i=0;i<glob_result.gl_pathc;++i){
        result.push_back(std::string(glob_result.gl_pathv[i]));
    }
    globfree(&glob_result);
    return result;
};

void SerialPort::Connect(){
    this->fd = open(this->portName.c_str(), O_RDWR | O_NOCTTY | O_SYNC);
    if(this->fd == -1) { // if open is unsucessful
        std::stringstream ss;
        ss << "Unable to open " << portName << ". Reason: " << strerror( errno );
        std::string message = ss.str();
        char *cstr = new char[message.length() + 1];
        strcpy(cstr, message.c_str());
        throw JavaIOException(cstr);
        delete [] cstr;
    } else {
        fcntl(this->fd, F_SETFL, 0);
        printf("Port '%s' has been sucessfully opened and '%d' is the file description\n", this->portName.c_str(), this->fd);
        fflush(stdout);
    }

    struct termios default_port_settings;
    struct termios port_settings;

    tcgetattr(0, &default_port_settings);
    if(this->baudRateIn < 0){ // if auto baud
        speed_t ispeed = cfgetispeed(&default_port_settings);
        this->baudRateIn = ispeed;
        //printf("Reading from Port '%s' with baud rate '%d' '%d'\n", this->portName.c_str(), this->baudRateIn, ispeed);
        //fflush(stdout);
    }else{
        //printf("Reading from Port '%s' with baud rate '%d' \n", this->portName.c_str(), this->baudRateIn);
        //fflush(stdout);
        cfsetispeed(&port_settings, this->baudRateIn);
    }
    if(this->baudRateOut < 0){ // if auto baud
        speed_t ospeed = cfgetospeed(&default_port_settings);
        this->baudRateOut = ospeed;
        //printf("Writing to Port '%s' with baud rate '%d' '%d'\n", this->portName.c_str(), this->baudRateOut, ospeed);
        //fflush(stdout);
    }else{
        cfsetospeed(&port_settings, this->baudRateOut);
        //printf("Writing to Port '%s' with baud rate '%d' \n", this->portName.c_str(), this->baudRateOut);
        //fflush(stdout);
    }

    // set no parity, stop bits, data bits
    port_settings.c_cc[VTIME]    = 0;   /* inter-character timer unused */
    port_settings.c_cc[VMIN]     = 1;
    port_settings.c_cflag &= ~PARENB;
    port_settings.c_cflag &= ~CSTOPB;
    port_settings.c_cflag &= ~CSIZE;
    port_settings.c_cflag |= CREAD | CLOCAL;
    port_settings.c_cflag |= CS8;

    // apply the settings to the port
    tcsetattr(this->fd, TCSANOW, &port_settings);
};

unsigned int SerialPort::Write(const char *data){
    int length = sizeof(data)/sizeof(char);
    ssize_t written = write(this->fd, data, length);
    return (unsigned int) written;
};

/*
void SerialPort::Read(std::function<void(char*)> callback) {
    while(this->fd!=NULL){
        char buffer[this->baudRateOut];
        int n = read(this->fd, &buffer, this->baudRateOut);
        buffer[n] = '\0';
        if(n>0){
            callback(buffer);
        }

        if(n<0){
            //std::cout << "n < 0" << std::endl << std::flush;
            std::stringstream ss;
            ss << "Unable continue to read. Reason: " << strerror(errno);
            std::string message = ss.str();
            //std::cout << message << std::endl << std::flush;
            char *cstr = new char[message.length() + 1];
            strcpy(cstr, message.c_str());
            if(this->fd!=NULL){
                throw JavaIOException(cstr);
            }
            delete [] cstr;
        }
    }
};
*/

std::string SerialPort::Read() {
    std::string result;
    if(this->fd){
        char buffer[this->baudRateOut];
        int n = read(this->fd, &buffer, this->baudRateOut);
        buffer[n] = '\0';

        if(n>0){
            result = buffer;
        }

        if(n<0){
            //std::cout << "n < 0" << std::endl << std::flush;
            std::stringstream ss;
            ss << "Unable continue to read. Reason: " << strerror(errno);
            std::string message = ss.str();
            //std::cout << message << std::endl << std::flush;
            char *cstr = new char[message.length() + 1];
            strcpy(cstr, message.c_str());
            if(this->fd!=0){
                throw JavaIOException(cstr);
            }
            //delete []cstr;
        }
    }
    return result;
};

void SerialPort::Disconnect(){
    close(this->fd);
    this->fd = 0;
};

#ifdef __cplusplus
}
#endif /* __cplusplus */