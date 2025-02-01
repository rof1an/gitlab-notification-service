package com.notification.service.dto;

import lombok.Data;

@Data
public class CreateTaskDto {

    private long id;

    private String title;

    private String linkToMr;

    private String status;

    private long developerId;

    private long reviewerId;
}
