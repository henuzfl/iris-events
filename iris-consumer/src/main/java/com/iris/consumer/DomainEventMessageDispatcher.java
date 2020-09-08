package com.iris.consumer;

/**
 * @Author: zfl
 * @Date: 2020/8/18 10:03
 * @Version: 1.0.0
 */
public class DomainEventMessageDispatcher {

    private DomainEventMessageConsumer domainEventMessageConsumer;

    public DomainEventMessageDispatcher(DomainEventMessageConsumer domainEventMessageConsumer) {
        this.domainEventMessageConsumer = domainEventMessageConsumer;
    }

    public void active(String dispatcherId,
                       DomainEventHandlers domainEventHandlers) {
        domainEventMessageConsumer.subscribe(dispatcherId, domainEventHandlers);
    }
}
