package com.metapatrol.jserialport.url.registration;

import org.junit.Test;

import java.io.IOException;
import java.net.URL;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class URLHandlerRegistrationTest {

    @Test
    public void test_should_accept_url() throws IOException {
        URL url = new URL("serialport:///dev/ttyACM0?baud_rate_in=4800&baud_rate_out=4800");
    }
}
