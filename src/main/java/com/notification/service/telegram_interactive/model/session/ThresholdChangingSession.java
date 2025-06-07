package com.notification.service.telegram_interactive.model.session;

import com.notification.service.telegram_interactive.model.ThresholdModel;
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
