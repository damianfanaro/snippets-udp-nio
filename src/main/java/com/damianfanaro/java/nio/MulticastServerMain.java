package com.damianfanaro.java.nio;

/**
 * TODO: brief comment about this class
 *
 * @author Damian Fanaro (damianfanaro@gmail.com)
 */
public class MulticastServerMain {

    public static void main(String [] args) {
        try {
            MulticastServer multicastServer = new MulticastServer(args[0], Integer.valueOf(args[1]));
            new Thread(multicastServer).start();

        } catch (Exception e) {
            System.out.println("Invalid arguments. Try [224.0.0.1, 4444]");
        }
    }

}
