package com.notification.service.bot.interactive.model.session;

import com.notification.service.bot.interactive.model.ThresholdModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ThresholdChangingSession {

    private ThresholdModel thresholdModel;
    private Step step;

    public enum Step {
        SELECT_THRESHOLD,
        COMPLETE
    }
}
