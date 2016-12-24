package com.metapatrol.jserialport;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class SerialPortConfiguration implements Cloneable {
    private Baud baudrateIn = Baud.AUTO;
    private Baud baudrateOut = Baud.AUTO;

    private SerialPortConfiguration(){}

    public Baud getBaudrateIn() {
        return baudrateIn;
    }

    public void setBaudrateIn(Baud baudrateIn) {
        this.baudrateIn = baudrateIn;
    }

    public Baud getBaudrateOut() {
        return baudrateOut;
    }

    public void setBaudrateOut(Baud baudrateOut) {
        this.baudrateOut = baudrateOut;
    }

    public final static SerialPortConfiguration DEFAULT = new SerialPortConfiguration(){
        {
            setBaudrateIn(Baud.AUTO);
            setBaudrateOut(Baud.AUTO);
        }
    };

    public SerialPortConfiguration clone() throws CloneNotSupportedException {
        return (SerialPortConfiguration) super.clone();
    }

    public static SerialPortConfigurationBuilder builder(){
        return new SerialPortConfigurationBuilder();
    }

    public static class SerialPortConfigurationBuilder {
        private SerialPortConfiguration c;
        private SerialPortConfigurationBuilder(){
            c = new SerialPortConfiguration();
        }

        public SerialPortConfigurationBuilder withBaudRateIn(Baud rateIn){
            c.setBaudrateIn(rateIn);
            return this;
        }

        public SerialPortConfigurationBuilder withBaudRateOut(Baud rateOut){
            c.setBaudrateOut(rateOut);
            return this;
        }
        public SerialPortConfiguration build(){
            return c;
        }
    }
}
