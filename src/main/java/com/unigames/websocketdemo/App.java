package com.unigames.websocketdemo;

import com.unigames.commons.inject.hk2.AutoInjectBinder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.tyrus.server.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Hello world!
 */
public class App {

    // Base URI the Grizzly HTTP server will listen on
    public static final String HTTP_BASE_URI = "http://0.0.0.0:8080/";

    public static final String WEBSOCKET_HOST_NAME = "0.0.0.0";
    public static final int WEBSOCKET_PORT = 9090;
    public static final String WEBSOCKET_CONTEXT_PATH = "/";

    public static Server createWebsocketServer() {
        return new Server(WEBSOCKET_HOST_NAME, WEBSOCKET_PORT, WEBSOCKET_CONTEXT_PATH, null, MyEndpoint.class);
    }

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer createHttpServer() {

        String packageName = App.class.getPackage().getName();

        // create a resource config that scans for JAX-RS resources and providers
        // in com.example package
        final ResourceConfig rc = new ResourceConfig()
                .packages(packageName)
                .register(new AutoInjectBinder(packageName))
                .register(MyContainerLifecycleListener.getInstance());


        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(HTTP_BASE_URI), rc, false);
    }

    public static void main(String[] args) {
        HttpServer httpServer = createHttpServer();
        Server websocketServer = createWebsocketServer();

        try {
            websocketServer.start();
            httpServer.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Please press a key to stop the server.");
            reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpServer.shutdownNow();
            websocketServer.stop();
        }
    }
}
