package com.metapatrol.jserialport.url;

import com.metapatrol.jserialport.url.net.SerialPortURLStreamHandlerFactory;

import java.net.URL;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public abstract class URLStreamHandlerRegistrar {
    static {
        URL.setURLStreamHandlerFactory(new SerialPortURLStreamHandlerFactory());
    }
}
