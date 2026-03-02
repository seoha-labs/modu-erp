package com.seohalabs.moduerp.organization.infrastructure.initializer;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AdminProperties.class)
public class InitializerConfig {}
