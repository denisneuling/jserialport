package com.metapatrol.jserialport.url.net;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class SerialPortURLStreamHandlerFactory implements URLStreamHandlerFactory {
    public static final String PROTOCOL = "serialport";

    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (PROTOCOL.equals(protocol)) {
            return new SerialPortURLStreamHandler();
        }
        return null;
    }
}
