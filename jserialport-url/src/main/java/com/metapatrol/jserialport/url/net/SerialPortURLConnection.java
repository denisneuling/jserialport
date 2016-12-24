package com.metapatrol.jserialport.url.net;

import com.metapatrol.jserialport.Baud;
import com.metapatrol.jserialport.SerialPort;
import com.metapatrol.jserialport.SerialPortConfiguration;
import com.metapatrol.jserialport.url.io.SerialPortInputStream;
import com.metapatrol.jserialport.url.io.SerialPortOutputStream;
import sun.reflect.generics.tree.MethodTypeSignature;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class SerialPortURLConnection extends URLConnection {
    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    private SerialPort serialPort;

    private String port;
    private SerialPortConfiguration serialPortConfiguration;

    private Runnable onClose;

    protected SerialPortURLConnection(URL url) throws MalformedURLException {
        super(url);

        this.serialPortConfiguration = parseConfiguration(url);
        if(serialPortConfiguration.getBaudrateIn().getRate() > 0 || serialPortConfiguration.getBaudrateIn().getRate() == -1){
            this.setDoInput(true);
        }else{
            this.setDoInput(false);
        }

        if(serialPortConfiguration.getBaudrateOut().getRate() > 0 || serialPortConfiguration.getBaudrateOut().getRate() == -1){
            this.setDoOutput(true);
        }else{
            this.setDoOutput(false);
        }
    }

    private SerialPortConfiguration parseConfiguration(URL url) throws MalformedURLException {
        this.port = url.getPath();
        if(port==null){
            throw new MalformedURLException("URL path has been espected, but none was given");
        }

        String query = url.getQuery();
        Map<String, String> params = query!= null ? extractParams(query) : new HashMap<String, String>();
        try {
            return processParams(params);
        }catch(Throwable throwable){
            throw new MalformedURLException(throwable.getMessage());
        }
    }

    @Override
    public void connect() throws IOException {
        serialPort = SerialPort.connect(port, serialPortConfiguration);
        this.connected = true;

        onClose = () -> {
            try {
                if(outputStream!=null){
                    try {
                        outputStream.close();
                    }catch (Throwable throwable){}
                }
                if(inputStream!=null){
                    try {
                        inputStream.close();
                    }catch (Throwable throwable){}
                }

                if(serialPort.isConnected()) {
                    serialPort.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        };
    }

    private class Az {
        public void a(){
            //byteArrayInputStream.
        }
    }
    public InputStream getInputStream() throws IOException {
        if(!this.connected){
            throw new IllegalStateException("SerialPort is not connected!");
        }

        if(!this.doInput){
            throw new IllegalStateException("SerialPort is not readable!");
        }

        if(inputStream==null){
            inputStream = new SerialPortInputStream(onClose, serialPort);
        }

        return inputStream;
    }

    public OutputStream getOutputStream() throws IOException {
        if(!this.connected){
            throw new IllegalStateException("SerialPort is not connected!");
        }

        if(!this.doOutput){
            throw new IllegalStateException("SerialPort is not writable!");
        }

        if(outputStream==null){
            outputStream = new SerialPortOutputStream(onClose, serialPort);
        }

        return outputStream;
    }

    private static Function<String, Baud> fnBaud = (in) -> {
        Supplier<String> message = () -> {
            String rates = Arrays.stream(Baud.values()).map(v -> ""+v.getRate()).collect(Collectors.joining(", "));
            return String.format("Baud '%s' is unknown. Valid rates are %s", in, rates);
        };
        Integer intValue;
        try {
            intValue = Integer.parseInt(in);
        }catch (Throwable throwable){
            throw new IllegalArgumentException(message.get());
        }
        for(Baud baud : Baud.values()){
            if(baud.getRate() == intValue){
                return baud;
            }
        }
        throw new IllegalArgumentException(message.get());
    };

    private static<T,R> Consumer<T> applyAndAccept(Function<? super T,? extends R> f, Consumer<R> c){
        return t -> c.accept(f.apply(t));
    }

    private static Map<String, BiConsumer<SerialPortConfiguration, String>> DEFAULT_CONFIGURATION_CONSUMERS = new HashMap<String, BiConsumer<SerialPortConfiguration, String>>(){{
        put("baud_rate_out", (c, s) -> applyAndAccept(fnBaud, c::setBaudrateOut).accept(s));
        put("baud_rate_in", (c, s) -> applyAndAccept(fnBaud, c::setBaudrateIn).accept(s));
    }};

    private static final String ENCODING = Charset.defaultCharset().name();
    private static Map<String, String> extractParams(String query){
        try {
            Map<String, String> params = new HashMap<String, String>();
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                String key = URLDecoder.decode(pair[0], ENCODING);
                String value = "";
                if (pair.length > 1) {
                    value = URLDecoder.decode(pair[1], ENCODING);
                }
                params.put(key, value);
            }
            return params;
        } catch (UnsupportedEncodingException ex) {
            throw new AssertionError(ex);
        }
    }

    private static SerialPortConfiguration processParams(Map<String, String> map) throws MalformedURLException{
        SerialPortConfiguration serialPortConfiguration;
        try {
            serialPortConfiguration = SerialPortConfiguration.DEFAULT.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        for(Map.Entry<String, String> e: map.entrySet()){
            try {
                Optional<BiConsumer<SerialPortConfiguration, String>> optional = Optional.ofNullable(DEFAULT_CONFIGURATION_CONSUMERS.get(e.getKey()));
                if (optional.isPresent()) {
                    optional.get().accept(serialPortConfiguration, e.getValue());
                } else {
                    throw new IllegalArgumentException(String.format("Param '%s' is not known by this protocol", e.getKey()));
                }
            }catch (Throwable throwable){
                throw new MalformedURLException(String.format("Error processing param '%s': %s", e.getKey(), throwable.getMessage()));
            }
        }
        return serialPortConfiguration;
    }
}
