package com.notification.service.repository;

import com.notification.service.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Task findTaskByDeveloperIdOrderById(Long id);

    List<Task> findAllByDeveloperId(Long developerId);

    List<Task> findAllByReviewerId(Long reviewerId);
}
