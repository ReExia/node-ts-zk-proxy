package com.salty.provider.core.register.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author setsuna
 * server配置
 */
@Configuration
@ConfigurationProperties(prefix = "server")
public class ServerConfig {

    /**
     * 服务地址
     */
    private String address;

    /**
     * 服务端口
     */
    private int port;

    /**
     * 注册路径
     */
    private String registryPath;

    /**
     * 注册项目名称
     */
    private String appName;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRegistryPath() {
        return registryPath;
    }

    public void setRegistryPath(String registryPath) {
        this.registryPath = registryPath;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
