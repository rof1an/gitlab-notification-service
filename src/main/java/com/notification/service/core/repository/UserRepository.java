package com.notification.service.core.repository;

import com.notification.service.core.entity.User;
import com.notification.service.core.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<List<User>> findByRole(UserRole role);
}
