package com.nichetti.domain.user.service.strategy.database;

import com.nichetti.domain.user.repository.UserRepository;
import com.nichetti.domain.user.service.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("mySQLDatabaseStrategy")
public class MySQLDatabaseStrategy implements DatabaseStrategy {

    private final UserRepository userRepository;

    public MySQLDatabaseStrategy(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteById(String userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public boolean isActive() {
        return isBusinessHour();
    }
}
