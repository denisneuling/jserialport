package com.metapatrol.jserialport;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public final class SerialPort implements Closeable {
    static {
        LibraryLoader.load();
    }

    private final Object monitor = new Object();

    private ByteBuffer address;

    private final String port;
    private final SerialPortConfiguration serialPortConfiguration;

    private SerialPort(String port, SerialPortConfiguration serialPortConfiguration) {
        this.port = port;
        this.serialPortConfiguration = serialPortConfiguration;
    }

    public static SerialPort connect(String port) throws IOException {
        return connect(port, SerialPortConfiguration.DEFAULT);
    }
    public static SerialPort connect(String port, SerialPortConfiguration serialPortConfiguration) throws IOException {
        assert port!=null : "port must not be null";
        assert serialPortConfiguration !=null : "serialPortConfiguration must not be null";

        SerialPort serialPort = new SerialPort(
            port
        ,   serialPortConfiguration
        );
        serialPort.connect();

        return serialPort;
    }

    public synchronized boolean isConnected(){ return address!=null; }

    private void connect() throws IOException {
        if(isConnected()){
            throw new IllegalStateException(String.format("SerialPort '%s' is already connected", port));
        }

        synchronized (monitor) {
            this.address = nativeConnect(
                port
            ,   serialPortConfiguration.getBaudrateIn().getRate()
            ,   serialPortConfiguration.getBaudrateOut().getRate()
            );
        }
    }

    public String read() throws IOException {
        if(!isConnected()){
            throw new IllegalStateException(String.format("SerialPort '%s' is not connected", port));
        }

        return nativeRead(address);
    }

    public int write(String data) throws IOException {
        if(!isConnected()){
            throw new IllegalStateException(String.format("SerialPort '%s' is not connected", port));
        }

        synchronized (monitor) {
            return nativeWrite(address, data);
        }
    }

    public void close() throws IOException {
        if(!isConnected()){
            throw new IllegalStateException(String.format("SerialPort '%s' is not connected", port));
        }

        synchronized (monitor) {
            nativeDisconnect(address);
            address = null;
        }
    }

    public String getPort() { return new String(port); }
    public SerialPortConfiguration getSerialPortConfiguration() {
        try {
            return serialPortConfiguration.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static List<String> availableSerialPorts(){
        return nativeAvailablePorts();
    }

    /////////////////////////// NATIVE ///////////////////////////
    private native static List<String> nativeAvailablePorts();

    private native ByteBuffer nativeConnect(String port, int baudRateIn, int baudRateOut) throws IOException;
    private native void nativeDisconnect(ByteBuffer pointer) throws IOException;

    private native int nativeWrite(ByteBuffer pointer, String data) throws IOException;
    private native String nativeRead(ByteBuffer pointer) throws IOException;
}
