package com.notification.service.bot.interactive.model;

import lombok.Data;

@Data
public class ThresholdModel {

    private String mrTitle;

    private String linkToMr;

    private Long developerId;

    private Long reviewerId;
}
