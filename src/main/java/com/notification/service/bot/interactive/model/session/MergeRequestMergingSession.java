package com.notification.service.bot.interactive.model.session;

import com.notification.service.bot.interactive.model.MergeRequestModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MergeRequestMergingSession {

    private MergeRequestModel mergeRequestModel;
    private Step step;

    public enum Step {
        SELECT_MERGE_REQUEST,
        COMPLETE
    }
}
