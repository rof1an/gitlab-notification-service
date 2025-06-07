package com.notification.service.telegram_interactive.model;

import lombok.Data;

@Data
public class ThresholdChangingModel {

    private String mrTitle;

    private String linkToMr;

    private Long developerId;

    private Long reviewerId;
}
