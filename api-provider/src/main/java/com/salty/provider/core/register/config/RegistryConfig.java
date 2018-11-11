package com.salty.provider.core.register.config;

import com.salty.provider.core.register.ZookeeperRegistryService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author setsuna
 */
@Configuration
@ConfigurationProperties(prefix = "registry")
public class RegistryConfig {

    /**
     * 注册地址
     */
    private String servers;

    /**
     * 超时
     */
    private int timeout;


    @Bean
    public ZookeeperRegistryService registryService(){
        return new ZookeeperRegistryService(servers, timeout);
    }

    public String getServers() {
        return servers;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
