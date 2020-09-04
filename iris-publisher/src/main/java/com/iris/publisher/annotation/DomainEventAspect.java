package com.iris.publisher.annotation;

import com.google.common.collect.Lists;
import com.iris.common.BaseAggregateRoot;
import com.iris.common.BaseDomainEvent;
import com.iris.publisher.IDomainEventPublisher;
import com.iris.publisher.annotation.DomainEvent;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: zfl
 * @Date: 2020/8/21 16:45
 * @Version: 1.0.0
 */
@Aspect
public class DomainEventAspect {

    @Autowired
    private IDomainEventPublisher domainEventPublisher;

    @Pointcut("@annotation(com.iris.publisher.annotation.DomainEvent)")
    private void pointcut() {
    }

    @AfterReturning(value = "pointcut() && @annotation(domainEvent)",
            returning =
                    "aggregateRoots")
    @SuppressWarnings("unchecked")
    public void publish(DomainEvent domainEvent, Object aggregateRoots) {
        List aggregateRootList = aggregateRoots instanceof Collection ?
                Lists.newArrayList((Collection) aggregateRoots) :
                Lists.newArrayList(aggregateRoots);
        List<BaseDomainEvent> events = new ArrayList<>();
        for (Class clazz : domainEvent.events()) {
            for (Object object : aggregateRootList) {
                BaseAggregateRoot aggregateRoot = (BaseAggregateRoot) object;
                Constructor constructor = null;
                try {
                    constructor =
                            clazz.getDeclaredConstructor(aggregateRoot.getClass
                                    ());
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                BaseDomainEvent event =
                        (BaseDomainEvent) BeanUtils.instantiateClass(constructor,
                                aggregateRoot);
                events.add(event);
            }
        }
        domainEventPublisher.publish(events);
    }
}
