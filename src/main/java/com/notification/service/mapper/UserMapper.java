package com.notification.service.mapper;

import com.notification.service.dto.UserDto;
import com.notification.service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User toModel(UserDto userDto);

    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);
}
