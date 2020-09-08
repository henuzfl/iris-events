package com.iris.consumer;

/**
 * @Author: zfl
 * @Date: 2020/8/18 10:44
 * @Version: 1.0.0
 */
public interface DomainEventMessageConsumer {

    void subscribe(String subscriberId, DomainEventHandlers handlers);
}
