package com.nichetti.domain.interceptor;

import com.nichetti.domain.user.interceptor.RateLimit;
import com.nichetti.domain.user.interceptor.RequestRateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestRateLimiterTest {

    @Mock
    private HandlerMethod handlerMethod;

    @Mock
    private RateLimit rateLimitAnnotation;

    private RequestRateLimiter requestRateLimiter;

    @BeforeEach
    void setUp() {
        requestRateLimiter = new RequestRateLimiter(null);
    }

    @Test
    void testPreHandleWithRateLimitAnnotation() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(handlerMethod.getMethodAnnotation(RateLimit.class)).thenReturn(rateLimitAnnotation);
        when(rateLimitAnnotation.limit()).thenReturn(10);
        when(rateLimitAnnotation.key()).thenReturn("testKey");

        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("userId", "testUserId");
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, pathVariables);

        boolean result = requestRateLimiter.preHandle(request, response, handlerMethod);

        assertTrue(result);

        assertTrue(requestRateLimiter.getRateLimitCache().containsKey("testKeytestUserId"));
    }

    @Test
    void testPreHandleWithoutRateLimitAnnotation() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(handlerMethod.getMethodAnnotation(RateLimit.class)).thenReturn(null);

        boolean result = requestRateLimiter.preHandle(request, response, handlerMethod);

        assertTrue(result);

        assertTrue(requestRateLimiter.getRateLimitCache().isEmpty());
    }

    @Test
    void testPreHandleRateLimitExceeded() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(handlerMethod.getMethodAnnotation(RateLimit.class)).thenReturn(rateLimitAnnotation);
        when(rateLimitAnnotation.limit()).thenReturn(2);
        when(rateLimitAnnotation.key()).thenReturn("testKey");

        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("userId", "testUserId");
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, pathVariables);

        Semaphore semaphore = new Semaphore(1);
        requestRateLimiter.getRateLimitCache().put("testKeytestUserId", semaphore);

        semaphore.acquire();

        boolean result = requestRateLimiter.preHandle(request, response, handlerMethod);

        assertFalse(result);

        assertEquals("Rate limit exceeded", response.getContentAsString());

        assertEquals(0, semaphore.availablePermits());
    }
}