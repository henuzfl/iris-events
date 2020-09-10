package com.iris.producer.impl;

import com.iris.common.EventMessage;
import com.iris.common.jdbc.CommonJdbcOperations;
import com.iris.common.util.JsonUtils;
import com.iris.producer.IDomainEventProducer;
import com.iris.producer.config.IrisKafkaProducerProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;

/**
 * @Author: zfl
 * @Date: 2020/9/8 18:00
 * @Version: 1.0.0
 */
public class DomainEventKafkaProducer implements IDomainEventProducer {

    private Logger logger = LoggerFactory.getLogger(getClass());


    private Producer<String, byte[]> producer;

    private final CommonJdbcOperations commonJdbcOperations;

    public DomainEventKafkaProducer(IrisKafkaProducerProperties kafkaProducerProperties, CommonJdbcOperations commonJdbcOperations) {
        this.commonJdbcOperations = commonJdbcOperations;
        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers",
                kafkaProducerProperties.getBootstrapServers());
        producerProps.put("acks", "all");
        producerProps.put("retries", 0);
        producerProps.put("batch.size", 16384);
        producerProps.put("linger.ms", 1);
        producerProps.put("buffer.memory", 33554432);
        producerProps.put("key.serializer", "org.apache.kafka.common" +
                ".serialization.StringSerializer");
        producerProps.put("value.serializer", "org.apache.kafka.common" +
                ".serialization.ByteArraySerializer");
        producer = new KafkaProducer<>(producerProps);
    }

    public void processEvent(final EventMessage eventMessage) {
        logger.info("send domain event to kafka:" + eventMessage);

        send(eventMessage)
                .whenCompleteAsync((o, throwable) -> {
                    if (null == throwable) {
                        commonJdbcOperations.setEventMessagePublished(eventMessage.getId());
                    } else {
                        logger.info("发送失败：{}", throwable);
                    }
                });
    }

    private CompletableFuture<?> send(EventMessage eventMessage) {
        CompletableFuture<Object> result = new CompletableFuture<>();
        producer.send(new ProducerRecord(eventMessage.getEventAggregateType(),
                        eventMessage.getEventAggregateId(),
                        JsonUtils.toJsonStr(eventMessage).getBytes()),
                (metadata, exception) -> {
                    if (null == exception) {
                        result.complete(metadata);
                    } else {
                        result.completeExceptionally(exception);
                    }
                });
        return result;
    }

}
