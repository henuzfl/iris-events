package com.iris.producer;

import com.iris.common.EventMessage;

/**
 * @Author: zfl
 * @Date: 2020/9/8 17:45
 * @Version: 1.0.0
 */
public interface IDomainEventProducer {
    void processEvent(EventMessage eventMessage);
}
