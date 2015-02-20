package ar.com.ucle.snippets.udpnio;

public class TestRunner {
    public static void main(String [] args) {
        MulticastReceiver receiver = MulticastReceiver.builder()
                .receivesFrom("226.1.1.1", 4321, "lo", (buffer) -> System.out.println("winnie"))
                .receivesFrom("226.1.1.1", 4321, "lo", (buffer) -> System.out.println("pooh!"))
                .build();

        new Thread(receiver).start();

    }
}
