package com.nichetti.domain.user.service.strategy.quota;

public interface QuotaStrategy {
    int getConsumedQuota(String userId);
    boolean consumeQuota(String userId);
}
