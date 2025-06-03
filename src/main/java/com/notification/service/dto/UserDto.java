package com.notification.service.dto;

import com.notification.service.model.UserRole;
import lombok.Data;

@Data
public class UserDto {

    private long id;

    private String username;

    private String gitlabId;

    private long telegramChatId;

    private UserRole role;
}
