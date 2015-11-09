package com.unigames.websocketdemo;

import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.Session;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * REST endpoints for internal control and monitor.
 */
@Path("/internal")
public class MyResource {

    @Inject
    private SessionManager sessionManager;

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @Path("test")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }

    @Path("broadcast")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String broadcast(String text) {
        sessionManager.broadcast(text);
        return "OK";
    }

    @Path("kickout")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String kickout(String sessionId) {
        Session session = sessionManager.get(sessionId);
        if (session != null) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "You are kicked out!"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Done";
        } else {
            return "NotFound";
        }
    }

    @Path("count")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String count() {
        return Integer.toString(sessionManager.getPlayerCount());
    }
}
