package com.damianfanaro.java.nio;

import com.damianfanaro.java.nio.multisource.ChannelSettings;
import com.damianfanaro.java.nio.multisource.Client;
import com.damianfanaro.java.nio.multisource.MulticastEndpoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static com.damianfanaro.java.nio.multisource.Client.builder;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientTest {

    public static final String MESSAGE = "I'm a message";

    @Mock
    private DatagramChannel datagramChannel;

    @Mock
    private SelectionKey selectionKey;

    private Selector selector;
    private volatile String message;
    private Client receiver;
    private ChannelSettings settings;

    @Before
    public void setUp() throws IOException {
        Consumer<ByteBuffer> consumer = (buffer) -> {
            int length = buffer.getInt();
            byte[] array = new byte[length];
            buffer.get(array);
            message = new String(array, 0, length);
        };
        receiver = spy(builder().receivesFrom("226.1.1.1", 4321, "lo", consumer).build());
        selector = Selector.open();

        doReturn(selector).when(receiver).createSelector();

        settings = new ChannelSettings(datagramChannel, consumer);
        selectionKey.attach(settings);

        doReturn(settings).when(receiver).createChannel(eq(selector), any(MulticastEndpoint.class));
        Set<SelectionKey> keys = new HashSet<>();
        keys.add(selectionKey);
        doReturn(keys.iterator()).when(receiver).getSelectionKeys(selector);
    }

    @Test
    public void receiverShouldInvokeConsumer() throws InterruptedException, IOException {
        when(datagramChannel.receive(any(ByteBuffer.class))).thenAnswer(invocation -> {
            ByteBuffer buffer = (ByteBuffer) invocation.getArguments()[0];
            buffer.putInt(MESSAGE.length());
            buffer.put(MESSAGE.getBytes());
            return null;
        });
        Thread thread = new Thread(receiver);
        thread.start();

        Thread.sleep(100);
        receiver.stop();

        assertEquals(MESSAGE, message);
        thread.join();
    }
}
