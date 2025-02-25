package com.notification.service.repository;

import com.notification.service.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Task findByDeveloperId(Long developerId);

    Task findByReviewerId(Long reviewerId);
}
