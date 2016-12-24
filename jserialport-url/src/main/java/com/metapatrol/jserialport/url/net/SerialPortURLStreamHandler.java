package com.metapatrol.jserialport.url.net;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class SerialPortURLStreamHandler extends URLStreamHandler {
    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        SerialPortURLConnection serialPortURLConnection = new SerialPortURLConnection(url);
        return serialPortURLConnection;
    }
}
