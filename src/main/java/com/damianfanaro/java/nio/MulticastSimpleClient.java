package com.damianfanaro.java.nio;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * TODO: Complete with description
 *
 * @author dfanaro
 */
public class MulticastSimpleClient {

    public static void main(String[] args) throws IOException {
        MulticastSocket socket = new MulticastSocket(4444);
        InetAddress group = InetAddress.getByName("224.0.0.1");
        socket.joinGroup(group);
        DatagramPacket datagramPacket;
        while (true) {
            byte[] buffer = new byte[256];
            datagramPacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(datagramPacket);
            String data = new String(datagramPacket.getData());
            System.out.println("Received: " + data);
        }
    }

}
