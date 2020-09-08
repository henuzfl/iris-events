package com.iris.producer.impl;

import com.iris.common.EventMessage;
import com.iris.common.jdbc.CommonJdbcOperations;
import com.iris.common.util.JsonUtils;
import com.iris.producer.IDomainEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * @Author: zfl
 * @Date: 2020/9/8 18:00
 * @Version: 1.0.0
 */
public class DomainEventKafkaProducer implements IDomainEventProducer {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private KafkaTemplate kafkaTemplate;

    private final CommonJdbcOperations commonJdbcOperations;

    public DomainEventKafkaProducer(CommonJdbcOperations commonJdbcOperations) {
        this.commonJdbcOperations = commonJdbcOperations;
    }

    public void processEvent(final EventMessage eventMessage) {
        logger.info("send domain event to kafka:" + eventMessage);

        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(eventMessage.getEventAggregateType(),
                        eventMessage.getEventAggregateId(),
                        JsonUtils.toJsonStr(eventMessage));
        future.addCallback(new ListenableFutureCallback<SendResult<String,
                String>>() {
            @Override
            public void onFailure(Throwable ex) {
                /**
                 * 发送失败，重试
                 */
                logger.info("发送失败：{}", ex);
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                /**
                 * 发送成功
                 */
                commonJdbcOperations.setEventMessagePublished(eventMessage.getId());
            }
        });
    }
}
