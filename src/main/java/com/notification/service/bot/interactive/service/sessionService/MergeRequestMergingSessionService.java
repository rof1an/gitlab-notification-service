package com.notification.service.bot.interactive.service.sessionService;

import com.notification.service.bot.interactive.model.MergeRequestModel;
import com.notification.service.bot.interactive.model.session.MergeRequestMergingSession;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MergeRequestMergingSessionService {
    private final Map<String, MergeRequestMergingSession> sessions = new ConcurrentHashMap<>();

    public MergeRequestMergingSession getOrCreateSession(String chatId) {
        return sessions.computeIfAbsent(chatId, id -> new MergeRequestMergingSession(
                new MergeRequestModel(),
                MergeRequestMergingSession.Step.SELECT_MERGE_REQUEST
        ));
    }

    public MergeRequestMergingSession getSession(String chatId) {
        return sessions.get(chatId);
    }

    public void clearSession(String chatId) {
        sessions.remove(chatId);
    }
}
