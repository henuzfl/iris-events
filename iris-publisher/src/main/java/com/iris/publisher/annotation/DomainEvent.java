package com.iris.publisher.annotation;

import com.iris.common.BaseDomainEvent;

import java.lang.annotation.*;

/**
 * @Author: zfl
 * @Date: 2020/8/21 16:44
 * @Version: 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DomainEvent {

    Class<? extends BaseDomainEvent>[] events();
}
