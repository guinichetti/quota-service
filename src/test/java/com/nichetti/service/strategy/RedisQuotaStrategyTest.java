package com.nichetti.service.strategy;

import com.nichetti.configuration.QuotaConfiguration;
import com.nichetti.domain.user.service.strategy.quota.RedisQuotaStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RedisQuotaStrategyTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private QuotaConfiguration quotaConfiguration;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisQuotaStrategy redisQuotaStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testConsumeQuotaUnderLimit() {
        String userId = "user1";
        String quotaKey = "quota:" + userId;
        when(quotaConfiguration.getQuotaKeyPrefix()).thenReturn("quota:");
        when(quotaConfiguration.getMaxRequestsPerUser()).thenReturn(10);
        when(valueOperations.get(quotaKey)).thenReturn("5");

        boolean result = redisQuotaStrategy.consumeQuota(userId);

        assertTrue(result);
        verify(valueOperations, times(1)).increment(quotaKey, 1);
    }

    @Test
    void testConsumeQuotaAtLimit() {
        String userId = "user1";
        String quotaKey = "quota:" + userId;
        when(quotaConfiguration.getQuotaKeyPrefix()).thenReturn("quota:");
        when(quotaConfiguration.getMaxRequestsPerUser()).thenReturn(10);
        when(valueOperations.get(quotaKey)).thenReturn("10");

        boolean result = redisQuotaStrategy.consumeQuota(userId);

        assertFalse(result);
        verify(valueOperations, never()).increment(quotaKey, 1);
    }

    @Test
    void testConsumeQuotaNoPreviousRequests() {
        String userId = "user1";
        String quotaKey = "quota:" + userId;
        when(quotaConfiguration.getQuotaKeyPrefix()).thenReturn("quota:");
        when(quotaConfiguration.getMaxRequestsPerUser()).thenReturn(10);
        when(valueOperations.get(quotaKey)).thenReturn(null);

        boolean result = redisQuotaStrategy.consumeQuota(userId);

        assertTrue(result);
        verify(valueOperations, times(1)).increment(quotaKey, 1);
    }

    @Test
    void testGetConsumedQuotaWithPreviousRequests() {
        String userId = "user1";
        String quotaKey = "quota:" + userId;
        when(quotaConfiguration.getQuotaKeyPrefix()).thenReturn("quota:");
        when(valueOperations.get(quotaKey)).thenReturn("5");

        int consumedQuota = redisQuotaStrategy.getConsumedQuota(userId);

        assertEquals(5, consumedQuota);
    }

    @Test
    void testGetConsumedQuotaNoPreviousRequests() {
        String userId = "user1";
        String quotaKey = "quota:" + userId;
        when(quotaConfiguration.getQuotaKeyPrefix()).thenReturn("quota:");
        when(valueOperations.get(quotaKey)).thenReturn(null);

        int consumedQuota = redisQuotaStrategy.getConsumedQuota(userId);

        assertEquals(0, consumedQuota);
    }

}