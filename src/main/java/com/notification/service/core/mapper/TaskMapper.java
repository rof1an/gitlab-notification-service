package com.notification.service.core.mapper;

import com.notification.service.core.dto.TaskDto;
import com.notification.service.core.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

    Task toModel(TaskDto taskDto);

    TaskDto toDto(Task task);

    List<TaskDto> toDtoList(List<Task> tasks);
}
