package com.damianfanaro.java.nio;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * UDP Server that sends messages read from the standard user input.
 *
 * @author Damian Fanaro (damianfanaro@gmail.com)
 */
public class MulticastServer implements Runnable {

    public static final long PACKAGE_QUANTITY = 5000;
    public static final long GAUSS_FORMULA = (PACKAGE_QUANTITY + 1) * (PACKAGE_QUANTITY / 2);
    
    private MulticastSocket socket;
    private InetAddress group;
    private String serverName;
    private String ip;
    private int port;

    public MulticastServer(String ip, int port, String serverName) {
        this.ip = ip;
        this.port = port;
        this.serverName = serverName;
        System.out.println("Total Gauss sum is: " + GAUSS_FORMULA);
    }

    @Override
    public void run() {
        establishMulticastConnection();
        Integer i = 1;
        while(i <= PACKAGE_QUANTITY) {
            String message = serverName + "-" + Integer.toString(i);
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, group, port);
            try {
                socket.send(packet);
                Thread.sleep(800);
            } catch (IOException e) {
                System.out.println("An I/O exception has occurred when sending data from the server");
            } catch (InterruptedException e) {
                System.out.println("The thread has been interrupted");
            }
            i++;
        }
    }

    private void establishMulticastConnection() {
        try {
            socket = new MulticastSocket(port);
            group = InetAddress.getByName(ip);
        } catch (IOException e) {
            System.out.println(String.format("The connection could not be established with IP: %s and PORT: %d", ip, port));
        }
    }
}
