package com.notification.service.core.entity;

import com.notification.service.core.model.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "link_to_mr", nullable = false)
    private String linkToMr;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status;

    @ManyToOne
    @JoinColumn(name = "developer_id", nullable = false)
    private User developer;

    @ManyToOne
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;
}
