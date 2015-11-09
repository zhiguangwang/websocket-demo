package com.unigames.websocketdemo;

import com.unigames.commons.inject.AutoInject;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wangzhiguang on 10/30/15.
 */
@AutoInject
public class SessionManager {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public void add(Session session) {
        sessions.put(session.getId(), session);
    }

    public Session get(String sessionId) {
        return sessions.get(sessionId);
    }

    public boolean remove(Session session) {
        return sessions.remove(session.getId()) != null;
    }

    public int getPlayerCount() {
        return sessions.size();
    }

    public Set<String> getAllSessionIds() {
        return sessions.keySet();
    }

    public void sendText(Session session, String text) {
        session.getAsyncRemote().sendText(text);
    }

    public void broadcast(String text) {
        getAllSessionIds().forEach(id -> {
            Session session = get(id);
            if (session != null && session.isOpen()) {
                sendText(session, text);
            }
        });
    }

    public void closeSession(Session session, CloseReason closeReason) {
        try {
            session.close(closeReason);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
