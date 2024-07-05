package com.nichetti.api;

import com.nichetti.domain.user.interceptor.RateLimit;
import com.nichetti.domain.user.mapper.UserMapper;
import com.nichetti.domain.user.service.UserService;
import com.nichetti.domain.user.service.model.User;
import com.nichetti.spec.api.UserResource;
import com.nichetti.spec.api.UserWithQuotaResource;
import com.nichetti.spec.api.UsersApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UsersApiImpl implements UsersApi {

    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public ResponseEntity<UserResource> createUser(UserResource userResource) {
        User user = userService.createUser(userResource);
        return ResponseEntity.status(201).body(userMapper.userToUserResource(user));
    }

    @Override
    public ResponseEntity<UserResource> updateUser(String userId, UserResource userResource) {
        User user = userService.updateUser(userId, userResource);
        return user != null
                ? ResponseEntity.ok(userMapper.userToUserResource(user))
                : ResponseEntity.status(404).build();
    }

    @Override
    public ResponseEntity<UserResource> getUser(String userId) {
        User user = userService.getUser(userId);
        return user != null
                ? ResponseEntity.ok(userMapper.userToUserResource(user))
                : ResponseEntity.status(404).build();
    }

    @Override
    public ResponseEntity<Void> deleteUser(String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @RateLimit(limit = 5)
    public ResponseEntity<Void> consumeQuota(String userId) {
        if (userService.consumeQuota(userId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @Override
    public ResponseEntity<List<UserWithQuotaResource>> getUsersQuota() {
        List<User> users = userService.getAllUsers();
        List<UserWithQuotaResource> userResources = userMapper.usersToUserWithQuotaResources(users);
        return ResponseEntity.ok(userResources);
    }
}
