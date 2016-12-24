package sun.net.www.protocol.serialport;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class SerialPortURLConnection extends com.metapatrol.jserialport.url.net.SerialPortURLConnection {
    protected SerialPortURLConnection(URL url) throws MalformedURLException {
        super(url);
    }
}
