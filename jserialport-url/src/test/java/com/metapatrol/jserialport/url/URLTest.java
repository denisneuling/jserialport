package com.metapatrol.jserialport.url;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class URLTest extends URLStreamHandlerRegistrar {

    @Test
    public void test_http_should_still_work() throws MalformedURLException {
        URL h = new URL("http://example.com");
    }

    @Test
    public void test_https_should_still_work() throws MalformedURLException {
        URL h = new URL("https://example.com");
    }

    @Test
    public void test_ftp_should_still_work() throws MalformedURLException {
        URL h = new URL("ftp://example.com");
    }

    @Test
    public void test_serialport_url() throws MalformedURLException {
        URL url = new URL("serialport:/tmp/exampleserialport");
    }
}
