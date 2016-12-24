package com.metapatrol.jserialport.url.io;

import com.metapatrol.jserialport.SerialPort;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class SerialPortInputStream extends InputStream {

    private boolean closing = false;
    private boolean closed = false;

    private Runnable onClose;
    private SerialPort serialPort;

    private byte[] data = new byte[0];
    private int position = 0;

    public SerialPortInputStream(Runnable onClose, SerialPort serialPort){
        this.onClose = onClose;
        this.serialPort = serialPort;
    }

    public void close() throws IOException {
        if(!closed && !closing) {
            closing = true;
            try {
                onClose.run();
            } catch (Throwable throwable) {
                Throwable cause = throwable.getCause();
                if (cause != null) {
                    if (cause instanceof IOException) {
                        throw (IOException) cause;
                    }
                }
                throw new RuntimeException(throwable.getMessage(), throwable);
            }finally {
                closing = false;
            }
            closed = true;
        }
    }

    public int available() throws IOException{
        return data.length - position;
    }

    @Override
    public int read() throws IOException {
        position++;
        if(position >= data.length) {
            position = 0;
            String largeString = serialPort.read();
            data = largeString.getBytes();
            return data.length<=0 ? 0 : data[position];
        }
        return data[position];
    }
}
