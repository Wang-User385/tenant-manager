package com.hand.hap.adaptor.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "custom.login")
public class LdapConfig {

    private Boolean loginSwitch;
    private String[] loginUrls;
}
