package com.metapatrol.jserialport.url.net;

import com.metapatrol.jserialport.url.URLStreamHandlerRegistrar;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class SerialPortURLConnectionTest extends URLStreamHandlerRegistrar {

    @Test
    public void test_serialport_url_no_params() throws MalformedURLException {
        URL url = new URL("serialport:/tmp/exampleserialport");
        SerialPortURLConnection c = new SerialPortURLConnection(url);
    }

    @Test
    public void test_serialport_url_params$baud_rate_in() throws MalformedURLException {
        URL url = new URL("serialport:/tmp/exampleserialport?baud_rate_in=4800");
        SerialPortURLConnection c = new SerialPortURLConnection(url);
    }

    @Test
    public void test_serialport_url_params$baud_rate_out() throws MalformedURLException {
        URL url = new URL("serialport:/tmp/exampleserialport?baud_rate_out=4800");
        SerialPortURLConnection c = new SerialPortURLConnection(url);
    }

    @Test
    public void test_serialport_url_params$baud_rate_in$baud_rate_out() throws MalformedURLException {
        URL url = new URL("serialport:/tmp/exampleserialport?baud_rate_in=4800&baud_rate_out=4800");
        SerialPortURLConnection c = new SerialPortURLConnection(url);
    }

    @Test
    public void test_serialport_url_params$baud_rate_out$baud_rate_in() throws MalformedURLException {
        URL url = new URL("serialport:/tmp/exampleserialport?baud_rate_out=4800&baud_rate_in=4800");
        SerialPortURLConnection c = new SerialPortURLConnection(url);
    }
}
