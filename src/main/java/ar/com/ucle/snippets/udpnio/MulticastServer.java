package ar.com.ucle.snippets.udpnio;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

/**
 * UDP Server that sends messages read from the standard user input.
 *
 * @author Damian Fanaro (damianfanaro@gmail.com)
 */
public class MulticastServer implements Runnable {
    
    private MulticastSocket socket;
    private String ip;
    private int port;
    private InetAddress group;

    public MulticastServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() {
        establishMulticastConnection();
        Scanner scanner = new Scanner(System.in);

        while(true) {
            byte[] input = scanner.nextLine().getBytes();
            DatagramPacket packet = new DatagramPacket(input, input.length, group, port);

            try {
                socket.send(packet);

            } catch (IOException e) {
                System.out.println("An I/O exception has occurred when sending data from the server");
            }
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
