package ar.com.ucle.snippets.udpnio;

/**
 * TODO: brief comment about this class
 *
 * @author Damian Fanaro (damianfanaro@gmail.com)
 * @date 21/02/15
 */
public class ServerInitMain {

    public static void main(String [] args) {
        Server.init(args[0], Integer.valueOf(args[1]));
    }
}
