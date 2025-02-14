package com.notification.service.entity;

import com.notification.service.model.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "developer_id", nullable = false)
    private User developer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    public Task(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.linkToMr = task.getLinkToMr();
        this.status = task.getStatus();
        this.linkToMr = task.getLinkToMr();
        this.developer = task.getDeveloper();
        this.reviewer = task.getReviewer();
    }
}
