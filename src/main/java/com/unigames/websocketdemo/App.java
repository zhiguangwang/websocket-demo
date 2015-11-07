package com.unigames.websocketdemo;

import org.glassfish.tyrus.server.Server;

import javax.websocket.server.ServerEndpointConfig;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Hello world!
 */
public class App {

    public static void runServer() {
        Server server = new Server("0.0.0.0", 9000, "/", null, MyEndpoint.class);

        try {
            server.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Please press a key to stop the server.");
            reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }

    public static void main(String[] args) {
        runServer();
    }
}
