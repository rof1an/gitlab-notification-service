package com.notification.service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskDto {

    private long id;

    private String title;

    private String linkToMr;

    private UserDto developer;

    private UserDto reviewer;
}
