package com.nichetti.domain.user.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

@Component
@Data
public class RequestRateLimiter implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RequestRateLimiter.class);

    private final Map<String, Semaphore> rateLimitCache = new ConcurrentHashMap<>();

    private final Environment environment;

    public RequestRateLimiter(Environment environment) {
        this.environment = environment;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RateLimit rateLimitAnnotation = handlerMethod.getMethodAnnotation(RateLimit.class);

        if (rateLimitAnnotation != null) {
            int limit = rateLimitAnnotation.limit();
            String keyPrefix = rateLimitAnnotation.key();

            Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            String userId = pathVariables.get("userId");

            String key = keyPrefix + userId;

            Semaphore semaphore = rateLimitCache.computeIfAbsent(key, k -> new Semaphore(limit));

            if (!semaphore.tryAcquire()) {
                logger.warn("Rate limit exceeded for key '{}'", key);
                response.getWriter().write("Rate limit exceeded");
                return false;
            }
        }

        return true;
    }
}
