package com.metapatrol.jserialport;

import com.metapatrol.jserialport.annotation.IntegrationTest;
import com.metapatrol.jserialport.util.StringUtil;
import org.junit.*;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Category(IntegrationTest.class)
public class SerialPortIntegrationIT {

    private String port = System.getProperty("serialport");

    @Before
    public void setUp() throws IOException {
        LibraryLoader.load();

        Assert.assertNotNull("System property 'serialport' must have been set!", port);
    }

    @Test
    public void test_listSerialPorts(){
        List<String> ports = SerialPort.availableSerialPorts();
        Assert.assertNotNull(ports);
    }

    @Test
    public void test_write() throws IOException {
        try(SerialPort serialPort = SerialPort.connect(port)){
            Assert.assertTrue(serialPort.isConnected());
            for(int i = 0 ; i < 10; i++){
                String d = StringUtil.randomString(i);
                Assert.assertEquals(d.length(), serialPort.write(d));
            }
        }

    }

    @Test
    public void test_read() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        try(SerialPort serialPort = SerialPort.connect(port)){
            Assert.assertTrue(serialPort.isConnected());
            Assert.assertNotNull(serialPort.read());
        }
    }
}
