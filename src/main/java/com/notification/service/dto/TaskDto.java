package com.notification.service.dto;

import lombok.Data;

@Data
public class TaskDto {

    private long id;

    private String title;

    private String linkToMr;

    private String status;

    private UserDto developer;

    private UserDto reviewer;
}
