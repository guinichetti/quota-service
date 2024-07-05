package com.nichetti.domain.user.service.strategy.database;

import com.nichetti.domain.user.repository.ElasticSearchRepository;
import com.nichetti.domain.user.service.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("elasticSearchDatabaseStrategy")
public class ElasticSearchDatabaseStrategy implements DatabaseStrategy {

    private final ElasticSearchRepository elasticSearchRepository;

    public ElasticSearchDatabaseStrategy(ElasticSearchRepository elasticSearchRepository) {
        this.elasticSearchRepository = elasticSearchRepository;
    }

    @Override
    public User findById(String userId) {
        return elasticSearchRepository.findById(userId);
    }

    @Override
    public User save(User user) {
        return elasticSearchRepository.save(user);
    }

    @Override
    public void deleteById(String userId) {
        elasticSearchRepository.deleteById(userId);
    }

    @Override
    public List<User> findAll() {
        return elasticSearchRepository.findAll();
    }

    @Override
    public boolean isActive() {
        return !isBusinessHour();
    }
}
