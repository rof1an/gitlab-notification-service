package com.notification.service.core.controller;

import com.notification.service.core.dto.UserDto;
import com.notification.service.core.entity.User;
import com.notification.service.core.mapper.UserMapper;
import com.notification.service.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper mapper;
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        User savedUser = userService.save(mapper.toModel(userDto));
        return mapper.toDto(savedUser);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        List<User> allUsers = userService.findAllUsers();
        return mapper.toDtoList(allUsers);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable("id") Long id) {
        User userById = userService.findById(id);
        return mapper.toDto(userById);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable("id") Long id, @RequestBody UserDto userDto) {
        User userModel = mapper.toModel(userDto);
        User updatedUser = userService.updateUserData(id, userModel);
        return mapper.toDto(updatedUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
    }
}
