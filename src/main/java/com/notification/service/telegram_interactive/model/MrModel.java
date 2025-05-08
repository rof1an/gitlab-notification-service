package com.notification.service.telegram_interactive.model;

import lombok.Data;

@Data
public class MrModel {

    private String title;

    private String linkToMr;

    private Long developerId;

    private Long reviewerId;
}
