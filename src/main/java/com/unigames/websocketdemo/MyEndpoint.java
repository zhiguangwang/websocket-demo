package com.unigames.websocketdemo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.tyrus.core.TyrusSession;

import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * Created by wangzhiguang on 10/30/15.
 */
@ServerEndpoint(value = "/", configurator = JerseyServerEndpointConfigurator.class)
public class MyEndpoint {

    private static final Logger log = LogManager.getLogger();

    private static String instanceId;

    @Inject
    private SessionManager sessionManager;

    static {
        instanceId = getInstanceId();
        log.debug("InstanceId: {}", instanceId);
    }

    private static String getInstanceId() {
        String instanceId = System.getProperty("instance.id");
        if (instanceId == null) {
            instanceId = getHostName();
        }
        return instanceId;
    }

    private static String getHostName() {
        try {
            return Inet4Address.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public MyEndpoint() {
        System.out.println("EchoEndpoint CREATED!");
    }

    @OnOpen
    public void onOpen(Session session) {

        log.debug("[{}] onOpen", session.getId());

        logRequestParameters(session);

        TyrusSession tyrusSession = (TyrusSession) session;
        String clientAddress = tyrusSession.getRemoteAddr();

        String sessionId = session.getId();
        log.debug("WebSocket opened for client {}: {}",
                clientAddress,
                sessionId);

        Map<String, List<String>> headers = tyrusSession.getHandshakeRequest().getHeaders();
        log.debug("user-agent: {}", String.join(", ", headers.get("user-agent")));

        log.debug("Idle Timeout: {}", session.getMaxIdleTimeout());

//        session.setMaxIdleTimeout(10000);

        log.debug("Idle Timeout: {}", session.getMaxIdleTimeout());

        String playerName = setplayerName(session);
        sessionManager.sendText(session, String.format("[%s] Welcome back, %s", instanceId, playerName));

        sessionManager.broadcast(String.format("%s has entered the room.", playerName));
        sessionManager.add(session);
    }

    private static void logRequestParameters(Session session) {
        log.debug("Logging request parameters for session {}", session.getId());
        session.getRequestParameterMap().forEach((key, values) -> {
            log.debug("{}: {}", key, String.join(", ", values));
        });
    }

    private static String setplayerName(Session session) {
        String playerName = session.getQueryString();
        if (playerName != null && playerName.length() > 0) {
            session.getUserProperties().put("name", playerName);
        } else {
            playerName = session.getId();
        }
        return playerName;
    }

    private static String getPlayerName(Session session) {
        String playerName = (String) session.getUserProperties().get("name");
        if (playerName == null) {
            playerName = session.getId();
        }
        return playerName;
    }


    private static boolean isAuthenticated(Session session) {
        Map<String, Object> userProperties = session.getUserProperties();
        Object authenticated = userProperties.get("auth");
        return authenticated != null;
    }

    private static void setAuthenticated(Session session) {
        session.getUserProperties().put("auth", new Object());
    }

    @OnMessage
    public String onMessage(Session session, String message) {

        log.debug("[{}] onMessage: {}",
                session.getId(),
                message);

        sessionManager.broadcast(String.format("%s: %s", getPlayerName(session), message));

        return null;
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        String sessionId = session.getId();
        log.debug("[{}] onClose: {}",
                sessionId,
                reason.getReasonPhrase());

        sessionManager.remove(session);

        sessionManager.broadcast(String.format("%s has left the room.", getPlayerName(session)));
    }
}
