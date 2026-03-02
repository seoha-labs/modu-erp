package com.seohalabs.moduerp.organization.infrastructure.initializer;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("erp.admin")
public record AdminProperties(String username, String password) {}
