package com.damianfanaro.java.nio.multisource;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class Client implements Runnable {

    private final List<MulticastEndpoint> endpoints;
    private volatile boolean active;

    private Client(List<MulticastEndpoint> endpoints) {
        this.endpoints = endpoints;
        active = true;
    }

    /**
     * MULTICAST RECEIVER BUILDER
     */
    public static class Builder {

        private List<MulticastEndpoint> endpoints;

        private Builder() {
            endpoints = new ArrayList<>(16);
        }

        public Builder receivesFrom(String groupAddress, int port, String nic, Consumer<ByteBuffer> consumer) {
            endpoints.add(new MulticastEndpoint(nic, groupAddress, port, consumer));
            return this;
        }

        public Client build() {
            return new Client(endpoints);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public void stop() {
        active = false;
    }

    @Override
    public void run() {
        try {
            Selector selector = createSelector();
            for (MulticastEndpoint endpoint : endpoints) {
                createChannel(selector, endpoint);
            }
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (active) {
                Iterator<SelectionKey> keys = getSelectionKeys(selector);
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    ChannelSettings settings = (ChannelSettings) key.attachment();
                    buffer.clear();
                    settings.getChannel().receive(buffer);
                    buffer.flip();
                    settings.getConsumer().accept(buffer);
                    keys.remove();
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error while reading sockets", e);
        }
    }

    public Iterator<SelectionKey> getSelectionKeys(Selector selector) throws IOException {
        int ready = selector.select();
        while (ready == 0) {
            ready = selector.select();
        }
        return selector.selectedKeys().iterator();
    }

    public Selector createSelector() throws IOException {
        return Selector.open();
    }

    public ChannelSettings createChannel(Selector selector, MulticastEndpoint endpoint) throws IOException {
        NetworkInterface nic = NetworkInterface.getByName(endpoint.getNic());
        InetAddress group = InetAddress.getByName(endpoint.getAddress());
        DatagramChannel dc = DatagramChannel.open(StandardProtocolFamily.INET)
                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .bind(new InetSocketAddress(endpoint.getPort()))
                .setOption(StandardSocketOptions.IP_MULTICAST_IF, nic);
        dc.configureBlocking(false);
        dc.join(group, nic);
        ChannelSettings settings = new ChannelSettings(dc, endpoint.getConsumer());
        dc.register(selector, SelectionKey.OP_READ, settings);
        return settings;
    }

}
