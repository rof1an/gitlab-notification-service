package com.notification.service.telegram_interactive.service;

import com.notification.service.telegram_interactive.model.ThresholdModel;
import com.notification.service.telegram_interactive.model.session.ThresholdCreationSession;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ThresholdCreationSessionService {

    private final Map<String, ThresholdCreationSession> sessions = new ConcurrentHashMap<>();

    public ThresholdCreationSession getOrCreateSession(String chatId) {
        return sessions.computeIfAbsent(chatId, id -> new ThresholdCreationSession(
                new ThresholdModel(),
                ThresholdCreationSession.Step.SELECT_MERGE_REQUEST)
        );
    }

    public ThresholdCreationSession getSession(String chatId) {
        return sessions.get(chatId);
    }

    public void clearSession(String chatId) {
        sessions.remove(chatId);
    }
}
