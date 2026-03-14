package com.seohalabs.moduerp.organization.shared.infrastructure.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("erp.admin")
public record AdminProperties(String username, String password) {}
