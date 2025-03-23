package com.notification.service.mapper;

import com.notification.service.dto.TaskDto;
import com.notification.service.entity.Task;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    Task toModel(TaskDto taskDto);

    TaskDto toDto(Task task);

    List<TaskDto> toDtoList(List<Task> tasks);
}
