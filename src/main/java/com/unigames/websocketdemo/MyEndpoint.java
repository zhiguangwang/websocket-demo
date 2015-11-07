package com.unigames.websocketdemo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.tyrus.core.TyrusSession;

import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;

/**
 * Created by wangzhiguang on 10/30/15.
 */
@ServerEndpoint(value = "/", configurator = HK2ServerEndpointConfigurator.class)
public class MyEndpoint {

    private static final Logger LOG = LogManager.getLogger();

    private static String instanceId;

    @Inject
    private SessionManager sessionManager;

    static {
        instanceId = getInstanceId();
        LOG.debug("InstanceId: {}", instanceId);
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

        LOG.debug("[{}] onOpen", session.getId());

        LOG.debug("Idle Timeout: {}", session.getMaxIdleTimeout());

//        session.setMaxIdleTimeout(10000);

        LOG.debug("Idle Timeout: {}", session.getMaxIdleTimeout());

        String playerName = setplayerName(session);
        sendText(session, String.format("[%s] Welcome back, %s", instanceId, playerName));

        broadcast(String.format("%s has entered the room.", playerName));
        sessionManager.add(session);

        TyrusSession tyrusSession = (TyrusSession) session;
        String clientAddress = tyrusSession.getRemoteAddr();

        String sessionId = session.getId();
        LOG.debug("WebSocket opened for client {}: {}",
                clientAddress,
                sessionId);
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


    private static boolean sendText(Session session, String text) {
        try {
            session.getBasicRemote().sendText(text);
            return true;
        } catch (IOException e) {
            closeSession(session, new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "IOException"));
            return false;
        }
    }

    private static void closeSession(Session session, CloseReason closeReason) {
        try {
            session.close(closeReason);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        LOG.debug("[{}] onMessage: {}",
                session.getId(),
                message);

        broadcast(String.format("%s: %s", getPlayerName(session), message));

        return null;
    }

    private void broadcast(String text) {
        sessionManager.getAllSessionIds().forEach(id -> {
            Session session = sessionManager.get(id);
            if (session != null && session.isOpen()) {
                sendText(session, text);
            }
        });
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        String sessionId = session.getId();
        LOG.debug("[{}] onClose: {}",
                sessionId,
                reason.getReasonPhrase());

        sessionManager.remove(session);

        broadcast(String.format("%s has left the room.", getPlayerName(session)));
    }
}
