package com.iris.publisher;

import com.iris.common.BaseDomainEvent;
import com.iris.common.EventMessage;
import com.iris.common.jdbc.CommonJdbcOperations;
import com.iris.common.util.JsonUtils;

import java.util.List;

/**
 * @Author: zfl
 * @Date: 2020/7/13 16:39
 * @Version: 1.0.0
 */
public class DomainEventPublisherImpl implements IDomainEventPublisher {

    private CommonJdbcOperations commonJdbcOperations;

    public DomainEventPublisherImpl(CommonJdbcOperations commonJdbcOperations) {
        this.commonJdbcOperations = commonJdbcOperations;
    }

    @Override
    public void publish(List<BaseDomainEvent> events) {
        events.stream().forEach(event ->
                commonJdbcOperations.insertEventMessage(makeEventMessageForDomainEvent(event))
        );
    }

    private static EventMessage makeEventMessageForDomainEvent(BaseDomainEvent event) {
        EventMessage eventMessage = new EventMessage();
        eventMessage.setMsgId(event.getId());
        eventMessage.setEventAggregateId(event.getAggregateId());
        eventMessage.setEventAggregateType(event.getAggregateType());
        eventMessage.setEventClassName(event.getEventClassName());
        eventMessage.setBody(JsonUtils.toJsonStr(event));
        return eventMessage;
    }
}
