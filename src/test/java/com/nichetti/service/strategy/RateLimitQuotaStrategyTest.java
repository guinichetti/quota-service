package com.nichetti.service.strategy;

import com.nichetti.configuration.QuotaConfiguration;
import com.nichetti.domain.user.interceptor.RequestRateLimiter;
import com.nichetti.domain.user.service.strategy.quota.RateLimitQuotaStrategy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.concurrent.Semaphore;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RateLimitQuotaStrategyTest {

    @Mock
    private QuotaConfiguration quotaConfiguration;

    @Mock
    private RequestRateLimiter requestRateLimiter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletRequestAttributes requestAttributes;

    @InjectMocks
    private RateLimitQuotaStrategy rateLimitQuotaStrategy;

    @Mock
    private Map<String, Semaphore> rateLimitCache;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        when(requestAttributes.getRequest()).thenReturn(request);
        when(requestAttributes.getResponse()).thenReturn(response);
        when(requestRateLimiter.getRateLimitCache()).thenReturn(rateLimitCache);
    }

    @Test
    void testConsumeQuota() throws Exception {
        String userId = "user1";
        when(request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE)).thenReturn(mock(HandlerMethod.class));
        when(requestRateLimiter.preHandle(any(HttpServletRequest.class), any(HttpServletResponse.class), any(HandlerMethod.class))).thenReturn(true);

        boolean result = rateLimitQuotaStrategy.consumeQuota(userId);

        assertTrue(result);
        verify(requestRateLimiter, times(1)).preHandle(any(HttpServletRequest.class), any(HttpServletResponse.class), any(HandlerMethod.class));
    }

    @Test
    void testConsumeQuotaException() throws Exception {
        String userId = "user1";
        when(request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE)).thenReturn(mock(HandlerMethod.class));
        when(requestRateLimiter.preHandle(any(HttpServletRequest.class), any(HttpServletResponse.class), any(HandlerMethod.class))).thenThrow(new RuntimeException("Exception"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            rateLimitQuotaStrategy.consumeQuota(userId);
        });

        assertEquals("Exception", exception.getCause().getMessage());
    }

    @Test
    void testGetConsumedQuotaWithSemaphore() {
        String userId = "user1";
        String key = "quota:" + userId;
        Semaphore semaphore = new Semaphore(5);
        when(rateLimitCache.get(key)).thenReturn(semaphore);
        when(quotaConfiguration.getQuotaKeyPrefix()).thenReturn("quota:");
        when(quotaConfiguration.getMaxRequestsPerUser()).thenReturn(10);

        int consumedQuota = rateLimitQuotaStrategy.getConsumedQuota(userId);

        assertEquals(5, consumedQuota);
    }

    @Test
    void testGetConsumedQuotaNoSemaphore() {
        String userId = "user1";
        String key = "quota:" + userId;
        when(quotaConfiguration.getQuotaKeyPrefix()).thenReturn("quota:");
        when(rateLimitCache.get(key)).thenReturn(null);

        int consumedQuota = rateLimitQuotaStrategy.getConsumedQuota(userId);

        assertEquals(0, consumedQuota);
    }
}