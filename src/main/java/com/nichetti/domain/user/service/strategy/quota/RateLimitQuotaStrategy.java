package com.nichetti.domain.user.service.strategy.quota;

import com.nichetti.configuration.QuotaConfiguration;
import com.nichetti.domain.user.interceptor.RequestRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import java.util.concurrent.Semaphore;

@Component("rateLimitQuotaStrategy")
@Profile("local")
public class RateLimitQuotaStrategy implements QuotaStrategy {

    private final QuotaConfiguration quotaConfiguration;
    private final RequestRateLimiter requestRateLimiter;


    @Autowired
    public RateLimitQuotaStrategy(QuotaConfiguration quotaConfiguration,
                                  RequestRateLimiter requestRateLimiter) {
        this.quotaConfiguration = quotaConfiguration;
        this.requestRateLimiter = requestRateLimiter;
    }


    @Override
    public boolean consumeQuota(String userId) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        try {
            return requestRateLimiter.preHandle(request, response, getHandlerMethod(request));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getConsumedQuota(String userId) {
        String key = quotaConfiguration.getQuotaKeyPrefix() + userId;
        Semaphore semaphore = requestRateLimiter.getRateLimitCache().get(key);
        if (semaphore != null) {
            return quotaConfiguration.getMaxRequestsPerUser() - semaphore.availablePermits();
        }
        return 0;
    }

    private HandlerMethod getHandlerMethod(HttpServletRequest request) {
        Object handler = request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);

        if (handler instanceof HandlerMethod) {
            return (HandlerMethod) handler;
        } else {
            return null;
        }
    }
}