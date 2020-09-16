package com.hx.cas.client.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

/**
 * @author Aaron
 * @description cas springboot 配置类
 * @date 2020/9/16
 */
@ConfigurationProperties(prefix = "cas", ignoreUnknownFields = false)
public class CasClientConfigProperties {
    /**
     * CAS 服务端 url，不能为空
     */
    @NotNull
    private String serverUrlPrefix;

    /**
     * CAS 服务端登录地址，serverUrlPrefix加上/login，不能为空
     */
    @NotNull
    private String serverLoginUrl;

    /**
     * 当前客户端的地址，不能为空
     */
    @NotNull
    private String serverName;

    /**
     * 忽略规则,访问那些地址不需要登录，如 /js/*|/images/*|/css/*
     */
    private String ignorePattern;

    /**
     * 自定义UrlPatternMatcherStrategy验证，必须全类名
     */
    private String ignoreUrlPatternType;

    public String getServerUrlPrefix() {
        return serverUrlPrefix;
    }

    public void setServerUrlPrefix(String serverUrlPrefix) {
        this.serverUrlPrefix = serverUrlPrefix;
    }

    public String getServerLoginUrl() {
        return serverLoginUrl;
    }

    public void setServerLoginUrl(String serverLoginUrl) {
        this.serverLoginUrl = serverLoginUrl;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getIgnorePattern() {
        return ignorePattern;
    }

    public void setIgnorePattern(String ignorePattern) {
        this.ignorePattern = ignorePattern;
    }

    public String getIgnoreUrlPatternType() {
        return ignoreUrlPatternType;
    }

    public void setIgnoreUrlPatternType(String ignoreUrlPatternType) {
        this.ignoreUrlPatternType = ignoreUrlPatternType;
    }
}
