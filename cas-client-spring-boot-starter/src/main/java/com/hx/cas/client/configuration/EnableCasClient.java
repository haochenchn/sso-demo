package com.hx.cas.client.configuration;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Aaron
 * @description 配置该注解开启cas功能
 * @date 2020/9/17
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(CasClientConfiguration.class)
public @interface EnableCasClient {
}
