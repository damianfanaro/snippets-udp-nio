package com.damianfanaro.java.nio.multisource;

import java.net.NetworkInterface;
import java.util.Enumeration;

public class ClientMain {

    public static void main(String [] args) {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while(networkInterfaces.hasMoreElements()) {
                NetworkInterface anInterface = networkInterfaces.nextElement();
                if(anInterface.isUp()) {
                    System.out.println("name=" + anInterface.getName() + " display=" + anInterface.getDisplayName() + " multicast=" + anInterface.supportsMulticast());
                }
            }
            Client receiver = Client.builder()
                    .receivesFrom(args[0], Integer.valueOf(args[1]), "enp3s0", (buffer) -> System.out.println(new String(buffer.array(), 0, buffer.position())))
                    .receivesFrom(args[2], Integer.valueOf(args[3]), "enp3s0", (buffer) -> System.out.println(new String(buffer.array(), 0, buffer.position())))
                    .build();
            new Thread(receiver).start();
            System.out.println("server initialized");
        } catch (Exception e) {
            System.out.println("Invalid arguments. Try [226.1.1.1, 4321, 224.1.1.1, 4322]");
        }
    }

}
