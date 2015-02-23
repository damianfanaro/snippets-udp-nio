package ar.com.ucle.snippets.udpnio;

import java.net.NetworkInterface;
import java.util.Enumeration;

public class ReceiverInit {

    public static void main(String [] args) {

        try {

            /**
             * Network interfaces listing
             */
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while(networkInterfaces.hasMoreElements()) {
                NetworkInterface anInterface = networkInterfaces.nextElement();
                if(anInterface.isUp()) {
                    System.out.println("name=" + anInterface.getName() + " display=" + anInterface.getDisplayName() + " multicast=" + anInterface.supportsMulticast());
                }
            }

            /**
             * Multicast receiver initialization
             */
            MulticastReceiver receiver = MulticastReceiver.builder()
                    .receivesFrom(args[0], Integer.valueOf(args[1]), "lo", (buffer) -> System.out.println("winnie"))
                    .receivesFrom(args[2], Integer.valueOf(args[3]), "lo", (buffer) -> System.out.println("pooh!"))
                    .build();

            new Thread(receiver).start();

            System.out.println("Server initialized!");

        } catch (Exception e) {
            System.out.println("Invalid arguments. Try [226.1.1.1, 4321, 224.1.1.1, 4322]");
        }
    }
}
