package ar.com.ucle.snippets.udpnio;

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

public class MulticastReceiver implements Runnable {
    static class MulticastEndpoint {
        private String nic;
        private String address;
        private int port;
        Consumer<ByteBuffer> consumer;

        MulticastEndpoint(String nic, String address, int port, Consumer<ByteBuffer> consumer) {
            this.nic = nic;
            this.address = address;
            this.port = port;
            this.consumer = consumer;
        }
    }

    static class ChannelSettings {
        private DatagramChannel channel;
        private Consumer<ByteBuffer> consumer;

        ChannelSettings(DatagramChannel channel, Consumer<ByteBuffer> consumer) {
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

    private final List<MulticastEndpoint> endpoints;
    private volatile boolean active;

    private MulticastReceiver(List<MulticastEndpoint> endpoints) {
        this.endpoints = endpoints;
        active = true;
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

    Iterator<SelectionKey> getSelectionKeys(Selector selector) throws IOException {
        int ready = selector.select();
        while (ready == 0) {
            ready = selector.select();
        }
        return selector.selectedKeys().iterator();
    }

    Selector createSelector() throws IOException {
        return Selector.open();
    }

    ChannelSettings createChannel(Selector selector, MulticastEndpoint endpoint) throws IOException {
        NetworkInterface interf = NetworkInterface.getByName(endpoint.nic);
        InetAddress group = InetAddress.getByName(endpoint.address);

        DatagramChannel dc = DatagramChannel.open(StandardProtocolFamily.INET)
                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .bind(new InetSocketAddress(endpoint.port))
                .setOption(StandardSocketOptions.IP_MULTICAST_IF, interf);
        dc.configureBlocking(false);

        dc.join(group, interf);
        ChannelSettings settings = new ChannelSettings(dc, endpoint.consumer);
        dc.register(selector, SelectionKey.OP_READ, settings);
        return settings;
    }

    public static class Builder {

        private List<MulticastEndpoint> endpoints;

        private Builder() {
            endpoints = new ArrayList<>(16);
        }

        public Builder receivesFrom(String groupAddress, int port, String nic, Consumer<ByteBuffer> consumer) {
            endpoints.add(new MulticastEndpoint(nic, groupAddress, port, consumer));
            return this;
        }

        public MulticastReceiver build() {
            return new MulticastReceiver(endpoints);
        }
    }
}
