package com.nichetti.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuotaConfiguration {

    @Value("${quota.keyPrefix:user:quota:}")
    private String quotaKeyPrefix;

    @Value("${quota.maxRequestsPerUser:5}")
    private int maxRequestsPerUser;

    public String getQuotaKeyPrefix() {
        return quotaKeyPrefix;
    }

    public int getMaxRequestsPerUser() {
        return maxRequestsPerUser;
    }
}