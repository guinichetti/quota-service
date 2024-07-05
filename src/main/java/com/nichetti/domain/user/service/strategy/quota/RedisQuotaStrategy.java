package com.nichetti.domain.user.service.strategy.quota;

import com.nichetti.configuration.QuotaConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component("redisQuotaStrategy")
@Profile("cloud")
public class RedisQuotaStrategy implements QuotaStrategy {

    private final RedisTemplate<String, String> redisTemplate;
    private final QuotaConfiguration quotaConfiguration;

    @Autowired
    public RedisQuotaStrategy(RedisTemplate<String, String> redisTemplate,
                              QuotaConfiguration quotaConfiguration) {
        this.redisTemplate = redisTemplate;
        this.quotaConfiguration = quotaConfiguration;
    }

    @Override
    public boolean consumeQuota(String userId) {
        String quotaKey = quotaConfiguration.getQuotaKeyPrefix() + userId;
        String requestCountStr = redisTemplate.opsForValue().get(quotaKey);
        int requestCount = requestCountStr != null ? Integer.parseInt(requestCountStr) : 0;
        if (requestCount < quotaConfiguration.getMaxRequestsPerUser()) {
            redisTemplate.opsForValue().increment(quotaKey, 1);
            return true;
        }
        return false;
    }

    @Override
    public int getConsumedQuota(String userId) {
        String quotaKey = quotaConfiguration.getQuotaKeyPrefix() + userId;
        String requestCountStr = redisTemplate.opsForValue().get(quotaKey);
        return requestCountStr != null ? Integer.parseInt(requestCountStr) : 0;
    }
}
