package com.aaron.cas.config;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.AssertionThreadLocalFilter;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Aaron
 * @description cas配置：spring环境
 * 主要是注册监听器、过滤器
 * @date 2020/9/16
 */
@Configuration
public class SpringCasConfig {

    // 是否启用CAS
    private static boolean casEnabled  = true;

    @Value("${cas.filter-mapping}")
    private String filterMapping;

    @Value("${cas.cas-server-url-prefix}")
    private String casServerUrlPrefix;

    @Value("${cas.cas-server-login-url}")
    private String casServerLoginUrl;

    @Value("${cas.server-name}")
    private String serverName;

    @Value("${cas.use-session}")
    private String useSession;

    @Value("${cas.redirect-after-validation}")
    private String redirectAfterValidation;

    /**
     * 用于实现单点登出功能
     */
    @Bean
    public ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> singleSignOutHttpSessionListener() {
        ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> listener = new ServletListenerRegistrationBean<>();
        listener.setEnabled(casEnabled);
        listener.setListener(new SingleSignOutHttpSessionListener());
        listener.setOrder(1);
        return listener;
    }

    /**
     * 该过滤器用于实现单点登出功能，单点退出配置，一定要放在其他filter之前
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public FilterRegistrationBean singleSignOutFilter() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new SingleSignOutFilter());
        filterRegistration.setEnabled(casEnabled);
        filterRegistration.addUrlPatterns(filterMapping);
        filterRegistration.addInitParameter("casServerUrlPrefix", casServerUrlPrefix);
        filterRegistration.addInitParameter("serverName", serverName);
        filterRegistration.setOrder(2);
        return filterRegistration;
    }

    /**
     * 该过滤器负责用户的认证工作
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public FilterRegistrationBean authenticationFilter() {

        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new AuthenticationFilter());
        filterRegistration.setEnabled(casEnabled);
        filterRegistration.addUrlPatterns(filterMapping);
        // casServerLoginUrl:cas服务的登陆url
        filterRegistration.addInitParameter("casServerLoginUrl", casServerLoginUrl);
        // 本项目登录ip+port
        filterRegistration.addInitParameter("serverName", serverName);
        filterRegistration.addInitParameter("useSession", useSession);
        filterRegistration.addInitParameter("redirectAfterValidation", redirectAfterValidation);
        filterRegistration.setOrder(4);
        return filterRegistration;
    }

    /**
     * 该过滤器负责对Ticket的校验工作
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public FilterRegistrationBean cas20ProxyReceivingTicketValidationFilter() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        Cas20ProxyReceivingTicketValidationFilter cas20ProxyReceivingTicketValidationFilter = new Cas20ProxyReceivingTicketValidationFilter();
        cas20ProxyReceivingTicketValidationFilter.setServerName(serverName);
        filterRegistration.setFilter(cas20ProxyReceivingTicketValidationFilter);
        filterRegistration.setEnabled(casEnabled);
        filterRegistration.addUrlPatterns(filterMapping);
        filterRegistration.addInitParameter("casServerUrlPrefix", casServerUrlPrefix);
        filterRegistration.addInitParameter("serverName", serverName);
        filterRegistration.setOrder(5);
        return filterRegistration;
    }


    /**
     * 该过滤器对HttpServletRequest请求包装，
     * 可通过HttpServletRequest的getRemoteUser()方法获得登录用户的登录名
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public FilterRegistrationBean httpServletRequestWrapperFilter() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new HttpServletRequestWrapperFilter());
        filterRegistration.setEnabled(casEnabled);
        filterRegistration.addUrlPatterns(filterMapping);
        filterRegistration.setOrder(6);
        return filterRegistration;
    }

    /**
     * 该过滤器使得可以通过org.jasig.cas.client.util.AssertionHolder来获取用户的登录名。
     * 比如AssertionHolder.getAssertion().getPrincipal().getName()。
     * 这个类把Assertion信息放在ThreadLocal变量中，这样应用程序不在web层也能够获取到当前登录信息
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public FilterRegistrationBean assertionThreadLocalFilter() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new AssertionThreadLocalFilter());
        filterRegistration.setEnabled(casEnabled);
        filterRegistration.addUrlPatterns(filterMapping);
        filterRegistration.setOrder(7);
        return filterRegistration;
    }
}
