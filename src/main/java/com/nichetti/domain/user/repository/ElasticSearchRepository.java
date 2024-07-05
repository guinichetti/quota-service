package com.nichetti.domain.user.repository;

import com.nichetti.domain.user.service.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ElasticSearchRepository {

    private final List<User> mockUsers = new ArrayList<>();

    public List<User> mockUsers() {
        return mockUsers;
    }

    public User findById(String userId) {
        System.out.println("ElasticSearch: Finding user with ID  " + userId);
        List<User> users = mockUsers();

        for (User user : users) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }

        return null;
    }

    public User save(User user) {
        System.out.println("ElasticSearch: Saving user " + user);

        if (user.getId() == null) {
            user.setId(UUID.randomUUID().toString());
            mockUsers.add(user);
        } else {
            Optional<User> existingUser = mockUsers.stream()
                    .filter(u -> u.getId().equals(user.getId()))
                    .findFirst();

            if (existingUser.isPresent()) {
                int index = mockUsers.indexOf(existingUser.get());
                mockUsers.set(index, user);
            } else {
                throw new IllegalArgumentException("User with ID " + user.getId() + " not found for update.");
            }
        }

        return user;
    }

    public void deleteById(String userId) {
        System.out.println("ElasticSearch: Deleting user with ID " + userId);

        Optional<User> userToRemove = mockUsers.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst();

        userToRemove.ifPresent(mockUsers::remove);
    }

    public List<User> findAll() {
        System.out.println("ElasticSearch: Getting all users");
        return mockUsers();
    }
}
