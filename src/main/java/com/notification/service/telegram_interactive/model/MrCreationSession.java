package com.notification.service.telegram_interactive.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MrCreationSession {

    private MrModel mrModel;
    private Step step;

    public enum Step {
        TITLE,
        LINK,
        SELECT_REVIEWER,
        SELECT_DEVELOPER,
        COMPLETE
    }
}
