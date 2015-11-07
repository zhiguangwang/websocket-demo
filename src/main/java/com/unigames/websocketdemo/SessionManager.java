package com.unigames.websocketdemo;

import com.unigames.commons.inject.AutoInject;

import javax.websocket.Session;
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

    public Set<String> getAllSessionIds() {
        return sessions.keySet();
    }

}
