package com.notification.service.telegram_interactive.service;

import com.notification.service.telegram_interactive.model.MergeRequestModel;
import com.notification.service.telegram_interactive.model.session.MergeRequestCreationSession;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MergeRequestSessionService {

    private final Map<String, MergeRequestCreationSession> sessions = new ConcurrentHashMap<>();

    public MergeRequestCreationSession getOrCreateSession(String chatId) {
        return sessions.computeIfAbsent(chatId, id -> new MergeRequestCreationSession(
                new MergeRequestModel(),
                MergeRequestCreationSession.Step.TITLE)
        );
    }

    public MergeRequestCreationSession getSession(String chatId) {
        return sessions.get(chatId);
    }

    public void clearSession(String chatId) {
        sessions.remove(chatId);
    }
}
