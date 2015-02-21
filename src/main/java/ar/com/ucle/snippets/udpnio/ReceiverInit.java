package ar.com.ucle.snippets.udpnio;

public class ReceiverInit {

    public static void main(String [] args) {

        try {

            /**
             * Multicast receiver initialization
             */
            MulticastReceiver receiver = MulticastReceiver.builder()
                    .receivesFrom(args[0], Integer.valueOf(args[1]), "lo", (buffer) -> System.out.println("winnie"))
                    .receivesFrom(args[2], Integer.valueOf(args[3]), "lo", (buffer) -> System.out.println("pooh!"))
                    .build();

            new Thread(receiver).start();

        } catch (Exception e) {
            System.out.println("Invalid arguments. Try [226.1.1.1, 4321, 224.1.1.1, 4322]");
        }
    }
}
