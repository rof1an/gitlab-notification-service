package com.notification.service.bot.interactive.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MergeRequestModel {

    private String title;

    private String linkToMr;

    private Long developerId;

    private Long reviewerId;
}
