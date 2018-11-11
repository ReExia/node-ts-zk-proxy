package com.salty.provider.core.annitations;

import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author : setsuna
 * Zk注册
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ZkRegister {

    /**
     * 除去域名的完整路径
     * 例如: /user/list
     * @return
     */
    String requestPath() default "";

    /**
     * 服务名称
     * 比如:helloService
     * @return
     */
    String serviceName() default "";

    /**
     * 方法类型:默认为GET
     * @return
     */
    String method() default "GET";
}
