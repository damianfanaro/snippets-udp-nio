package com.damianfanaro.java.nio.multisource;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * TODO: Complete with description
 *
 * @author dfanaro
 */
public class MulticastEndpoint {

    private String nic;
    private String address;
    private int port;
    private Consumer<ByteBuffer> consumer;

    MulticastEndpoint(String nic, String address, int port, Consumer<ByteBuffer> consumer) {
        this.nic = nic;
        this.address = address;
        this.port = port;
        this.consumer = consumer;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Consumer<ByteBuffer> getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer<ByteBuffer> consumer) {
        this.consumer = consumer;
    }

}