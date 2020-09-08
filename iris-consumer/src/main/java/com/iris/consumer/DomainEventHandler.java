package com.iris.consumer;

import com.iris.common.EventMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

/**
 * @Author: zfl
 * @Date: 2020/8/18 10:45
 * @Version: 1.0.0
 */
@AllArgsConstructor
@Getter
public class DomainEventHandler {
    private String aggregateType;
    private String domainEventClass;
    private Consumer<EventMessage> handler;

    public boolean isHandle(EventMessage eventMessage) {
        return StringUtils.equals(aggregateType,
                eventMessage.getEventAggregateType()) && StringUtils.equals(domainEventClass, eventMessage.getEventClassName());
    }
}
