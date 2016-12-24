package com.metapatrol.jserialport.url.io;

import com.metapatrol.jserialport.Baud;
import com.metapatrol.jserialport.SerialPort;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class SerialPortOutputStream extends OutputStream {

    private boolean closing = false;
    private boolean closed = false;

    private Runnable onClose;
    private ByteArrayOutputStream byteArrayOutputStream;
    private SerialPort serialPort;
    private int bufferSize = -1;

    public SerialPortOutputStream(Runnable onClose, SerialPort serialPort){
        this.onClose = onClose;
        this.serialPort = serialPort;

        Baud baudRate = serialPort.getSerialPortConfiguration().getBaudrateOut();
        if(baudRate.getRate()>=0){
            bufferSize = baudRate.getRate();
            byteArrayOutputStream = new ByteArrayOutputStream(bufferSize);
        }else{
            byteArrayOutputStream = new ByteArrayOutputStream();
        }
    }

    @Override
    public void write(int i) throws IOException {
        byteArrayOutputStream.write(i);
        //autoflush to deallocate memory and exploit baud rate?
        if(bufferSize>=-1 && byteArrayOutputStream.size() >= bufferSize){
            flush();
        }
    }

    public void flush() throws IOException {
        if(byteArrayOutputStream.size()>0){
            String largeString = new String(byteArrayOutputStream.toByteArray(), Charset.defaultCharset().name());
            serialPort.write(largeString);
            byteArrayOutputStream.reset();
        }
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
}
