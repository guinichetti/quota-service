package com.nichetti.domain.user.service;

import com.nichetti.domain.user.service.model.User;
import com.nichetti.domain.user.service.strategy.quota.QuotaStrategy;
import com.nichetti.domain.user.service.strategy.database.DatabaseStrategy;
import com.nichetti.spec.api.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class UserService {

    private final List<DatabaseStrategy> databaseStrategies;
    private final QuotaStrategy quotaStrategy;

    @Autowired
    public UserService(List<DatabaseStrategy> databaseStrategies,
                       QuotaStrategy quotaStrategy) {
        this.databaseStrategies = databaseStrategies;
        this.quotaStrategy = quotaStrategy;
    }

    public User getUser(String userId) {
        User user = getDatabaseStrategy().findById(userId);

        if (user != null) {
            user.setRequestCount(quotaStrategy.getConsumedQuota(user.getId()));
        }

        return user;
    }

    public User createUser(UserResource userResource) {
        User user = new User();
        user.setFirstName(userResource.getFirstName());
        user.setLastName(userResource.getLastName());
        user.setLastLoginTimeUtc(OffsetDateTime.now());
        return getDatabaseStrategy().save(user);
    }

    public User updateUser(String userId, UserResource userResource) {
        User user = getDatabaseStrategy().findById(userId);

        if (user != null) {
            user.setFirstName(userResource.getFirstName());
            user.setLastName(userResource.getLastName());
            user.setLastLoginTimeUtc(OffsetDateTime.now());
            getDatabaseStrategy().save(user);
            return user;
        }

        return null;
    }

    public void deleteUser(String userId) {
        getDatabaseStrategy().deleteById(userId);
    }

    public boolean consumeQuota(String userId) {
        return quotaStrategy.consumeQuota(userId);
    }

    public List<User> getAllUsers() {
        List<User> users = getDatabaseStrategy().findAll();

        for (User user : users) {
            user.setRequestCount(quotaStrategy.getConsumedQuota(user.getId()));
        }

        return users;
    }

    public DatabaseStrategy getDatabaseStrategy() {
        return this.databaseStrategies.stream()
                .filter(DatabaseStrategy::isActive)
                .findFirst().orElseThrow();
    }
}
