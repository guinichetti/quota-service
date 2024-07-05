package com.nichetti.domain.user.mapper;

import com.nichetti.domain.user.service.model.User;
import com.nichetti.spec.api.UserResource;
import com.nichetti.spec.api.UserWithQuotaResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({
            @Mapping(target = "id", source = "user.id"),
            @Mapping(target = "firstName", source = "user.firstName"),
            @Mapping(target = "lastName", source = "user.lastName"),
            @Mapping(target = "lastLoginTimeUtc", source = "user.lastLoginTimeUtc"),
    })
    UserResource userToUserResource(User user);
    @Mappings({
            @Mapping(target = "id", source = "user.id"),
            @Mapping(target = "firstName", source = "user.firstName"),
            @Mapping(target = "lastName", source = "user.lastName"),
            @Mapping(target = "lastLoginTimeUtc", source = "user.lastLoginTimeUtc"),
            @Mapping(target = "requestCount", source = "user.requestCount"),
    })
    UserWithQuotaResource userToUserWithQuotaResource(User user);
    List<UserWithQuotaResource> usersToUserWithQuotaResources(List<User> users);
}
