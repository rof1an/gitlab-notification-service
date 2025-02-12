package com.notification.service.repository;

import com.notification.service.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByDeveloperId(Long developerId);

    List<Task> findAllByReviewerId(Long reviewerId);
}
