package com.notification.service.mapper;

import com.notification.service.dto.TaskDto;
import com.notification.service.entity.Task;
import com.notification.service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "title", source = "taskDto.title")
    @Mapping(target = "linkToMr", source = "taskDto.linkToMr")
    @Mapping(target = "developer", source = "taskDto.developer")
    @Mapping(target = "reviewer", source = "taskDto.reviewer")
    Task toModel(TaskDto taskDto);

    TaskDto toDto(Task task);

    List<Task> toModelList(List<TaskDto> taskDtos);

    List<TaskDto> toDtoList(List<Task> tasks);

    @Named("idToUser")
    default User idToUser(Long taskId) {
        return User.builder()
                .id(taskId)
                .build();
    }
}
