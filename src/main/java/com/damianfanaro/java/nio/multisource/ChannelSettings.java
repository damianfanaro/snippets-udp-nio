package com.damianfanaro.java.nio.multisource;

import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.function.Consumer;

/**
 * TODO: Complete with description
 *
 * @author dfanaro
 */
public class ChannelSettings {

    private DatagramChannel channel;
    private Consumer<ByteBuffer> consumer;

    public ChannelSettings(DatagramChannel channel, Consumer<ByteBuffer> consumer) {
        this.channel = channel;
        this.consumer = consumer;
    }

    public Consumer<ByteBuffer> getConsumer() {
        return consumer;
    }

    public DatagramChannel getChannel() {
        return channel;
    }

}