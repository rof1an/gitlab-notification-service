package com.notification.service.bot.interactive.service.sessionService;

import com.notification.service.bot.interactive.model.ThresholdModel;
import com.notification.service.bot.interactive.model.session.ThresholdChangingSession;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ThresholdChangingService {

    private final Map<String, ThresholdChangingSession> sessions = new ConcurrentHashMap<>();

    public ThresholdChangingSession getOrCreateSession(String chatId) {
        return sessions.computeIfAbsent(chatId, id -> new ThresholdChangingSession(
                new ThresholdModel(),
                ThresholdChangingSession.Step.SELECT_THRESHOLD
        ));
    }

    public ThresholdChangingSession getSession(String chatId) {
        return sessions.get(chatId);
    }

    public void clearSession(String chatId) {
        sessions.remove(chatId);
    }
}
