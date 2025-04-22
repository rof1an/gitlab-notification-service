package com.notification.service.telegram_interactive.service;

import com.notification.service.telegram_interactive.model.MrCreationSession;
import com.notification.service.telegram_interactive.model.MrModel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MrSessionService {

    private final Map<String, MrCreationSession> sessions = new ConcurrentHashMap<>();

    public MrCreationSession getOrCreateSession(String chatId) {
        return sessions.computeIfAbsent(chatId, id -> new MrCreationSession(
                new MrModel(),
                MrCreationSession.Step.TITLE)
        );
    }

    public MrCreationSession getSession(String chatId) {
        return sessions.get(chatId);
    }

    public void clearSession(String chatId) {
        sessions.remove(chatId);
    }
}
