package com.notification.service.entity;

import com.notification.service.model.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "notifications")
@AllArgsConstructor
@NoArgsConstructor
public class NotificationData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role;

    private String title;

    private String linkToMr;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private Long developerId;

    private Long reviewerId;
}