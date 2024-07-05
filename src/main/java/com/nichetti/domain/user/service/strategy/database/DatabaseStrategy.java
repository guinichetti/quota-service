package com.nichetti.domain.user.service.strategy.database;

import com.nichetti.domain.user.service.model.User;

import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

public interface DatabaseStrategy {
    User findById(String userId);
    User save(User user);
    void deleteById(String userId);
    List<User> findAll();
    boolean isActive();

    default boolean isBusinessHour() {
        LocalTime now = LocalTime.now(ZoneOffset.UTC);
        return !now.isBefore(LocalTime.of(9, 0)) && now.isBefore(LocalTime.of(17, 0));
    }
}