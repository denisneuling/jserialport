/*
 * File:   SerialPort.h
 * Author: Denis Neuling (denisneuling@gmail.com)
 *
 * Created on 24. Dezember 2016, 09:01
 */

#ifndef SerialPort_H
#define SerialPort_H

#include <functional>
#include <termios.h>
#include <vector>
#include <string>

#ifdef __cplusplus
extern "C" {
#endif

extern const speed_t BAUDRATES[];

class SerialPort {
private:
    int fd;
    std::string portName;
    int baudRateIn;
    int baudRateOut;
public:
    SerialPort(std::string portName, int baudRateIn, int baudRateOut);
    ~SerialPort();

    static std::vector<std::string> GetAvailablePortNames();

    void Connect();
    unsigned int Write(const char *data);
    //void Read(std::function<void(char*)> callback);
    std::string Read();
    void Disconnect();
};

#ifdef __cplusplus
}
#endif /* __cplusplus */

#endif /* SerialPort_H */

