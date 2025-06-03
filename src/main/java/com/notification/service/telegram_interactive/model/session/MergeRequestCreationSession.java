package com.notification.service.telegram_interactive.model.session;

import com.notification.service.telegram_interactive.model.MergeRequestModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MergeRequestCreationSession {

    private MergeRequestModel mergeRequestModel;
    private Step step;

    public enum Step {
        TITLE,
        LINK,
        SELECT_REVIEWER,
        SELECT_DEVELOPER,
        COMPLETE
    }
}
