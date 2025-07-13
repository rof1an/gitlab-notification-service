package com.notification.service.repository;

import com.notification.service.entity.Task;
import com.notification.service.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByDeveloperId(Long developerId);

    Optional<Task> findByReviewerId(Long reviewerId);

    Optional<Task> findTaskByLinkToMr(String link);

    Optional<List<Task>> findAllByStatus(TaskStatus status);

    List<Task> findAllByStatusIn(List<TaskStatus> status);
}
