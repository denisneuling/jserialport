package com.metapatrol.jserialport.url.registration;

import com.metapatrol.jserialport.url.registration.annotation.IntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Category(IntegrationTest.class)
public class URLSerialPortIntegrationIT {

    private String port = System.getProperty("serialport");

    @Before
    public void setUp() throws IOException {
        Assert.assertNotNull("System property 'serialport' must have been set!", port);
    }

    @Test
    public void test_write() throws IOException {
        URLConnection connection = new URL(String.format("serialport:%s", port)).openConnection();
        connection.connect();
        try(OutputStream outputStream = connection.getOutputStream()){
            outputStream.write(new byte[0]);
            outputStream.write(new byte[]{1,2,3,4});
        }
    }

    @Test
    public void test_read() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        URLConnection connection = new URL(String.format("serialport:%s", port)).openConnection();
        connection.connect();
        try(InputStream inputStream = connection.getInputStream()){
            Assert.assertNotNull(inputStream.available());
            Assert.assertTrue(0 >= inputStream.available());
            Assert.assertTrue(0 >= inputStream.read());
        }
    }
}
