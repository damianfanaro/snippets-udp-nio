package ar.com.ucle.snippets.udpnio;

/**
 * TODO: brief comment about this class
 *
 * @author Damian Fanaro (damianfanaro@gmail.com)
 * @date 21/02/15
 */
public final class Server {
    
    public static void init(String ip, int port) {
        try {
            MulticastServer multicastServer = new MulticastServer(ip, port);
            new Thread(multicastServer).start();

        } catch (Exception e) {
            System.out.println("Invalid arguments. Try [224.1.1.1, 4322]");
        }
    }
}
