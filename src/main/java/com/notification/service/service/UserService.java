package com.notification.service.service;

import com.notification.service.entity.User;
import com.notification.service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
    }

    public User findUserByTelegramUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User with username " + username + " not found"));
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User updateUserData(long id, User newUserData) {
        User existingUser = findById(id);

        Optional.ofNullable(newUserData.getUsername())
                .ifPresent(existingUser::setUsername);

        Optional.ofNullable(newUserData.getGitlabId())
                .ifPresent(existingUser::setGitlabId);

        Optional.ofNullable(newUserData.getRole())
                .ifPresent(existingUser::setRole);

        return userRepository.save(existingUser);
    }

    public void updateUserDataByTelegramUsername(String username, Long telegramChatId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username = " + username));

        user.setTelegramChatId(telegramChatId);
        userRepository.save(user);
    }

    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }
}
