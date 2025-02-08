package com.cerebra.fileprocessor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Data
@Configuration
@ConfigurationProperties(prefix = "cerebra")
public class ConfigProperties implements Serializable {

    private String openapiDescription;
    private String openapiEmail;
    private String openapiLicense;
    private String openapiName;
    private String openapiTermsOfService;
    private String openapiTitle;
    private String openapiUrl;
    private String openApiServerUrl;

    private String userEmail;

    private Long tokenExpirationMs;
    private Long tokenRefreshExpirationMs;
    private String tokenSecretKey;

    private String mailHost;
    private String mailPort;
    private String mailUserName;
    private String mailPassword;

    private String corsAllowUrls;
    private String corsAllowHeaders;
    private String corsAllowMethods;

    private String logSecureAttribute;

    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
    private String threadNamePrefix;

    private String uploadDirectory;
    private String stompAccessUrl;
}
