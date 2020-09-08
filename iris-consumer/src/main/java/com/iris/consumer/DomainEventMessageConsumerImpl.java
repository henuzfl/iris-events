package com.iris.consumer;

import com.iris.common.EventMessage;
import com.iris.common.jdbc.CommonJdbcOperations;
import com.iris.common.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zfl
 * @Date: 2020/8/18 16:06
 * @Version: 1.0.0
 */
@Slf4j
public class DomainEventMessageConsumerImpl implements DomainEventMessageConsumer {

    @Autowired
    private KafkaProperties kafkaProperties;

    private final ScheduledExecutorService scheduledExecutorService;

    private final CommonJdbcOperations commonJdbcOperations;

    public DomainEventMessageConsumerImpl(CommonJdbcOperations commonJdbcOperations) {
        this.scheduledExecutorService =
                Executors.newSingleThreadScheduledExecutor();
        this.commonJdbcOperations = commonJdbcOperations;
    }

    @Override
    public void subscribe(String subscriberId, DomainEventHandlers handlers) {
        Properties consumerProperties = new Properties();
        consumerProperties.put("bootstrap.servers",
                kafkaProperties.getBootstrapServers());
        consumerProperties.put("group.id", subscriberId);
        consumerProperties.put("enable.auto.commit", "true");
        consumerProperties.put("session.timeout.ms", "30000");
        consumerProperties.put("key.deserializer", "org.apache.kafka.common" +
                ".serialization.StringDeserializer");
        consumerProperties.put("value.deserializer", "org.apache.kafka.common" +
                ".serialization.StringDeserializer");
        consumerProperties.put("auto.offset.reset", "earliest");
        KafkaConsumer<String, String> consumer =
                new KafkaConsumer<>(consumerProperties);
        consumer.subscribe(handlers.getAllAggregateTypes());
        scheduledExecutorService.scheduleAtFixedRate(() -> process(subscriberId, consumer,
                handlers), 0, 500, TimeUnit.MILLISECONDS);
    }

    private void process(String subscriberId, KafkaConsumer consumer,DomainEventHandlers handlers) {
        try {
            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(500));
            if (!records.isEmpty()) {
                for (ConsumerRecord<String, String> record : records) {
                    log.info("processing record topic: {} , key: {}, offset: " +
                                    "{}, " +
                                    "value: {}", record.topic(),
                            record.key(),
                            record.offset(), record.value());
                    EventMessage eventMessage =
                            JsonUtils.toObject(record.value(),
                                    EventMessage.class);
                    /**
                     * 如果已经处理过该消息，则放弃
                     */
                    if (isDuplicate(subscriberId,
                            eventMessage.getMsgId())) {
                        continue;
                    }
                    handlers.getHandlers().stream()
                            .filter(handler -> handler.isHandle(eventMessage))
                            .forEach(handler -> {
                                handler.getHandler().accept(eventMessage);
                            });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isDuplicate(String consumerId, String msgId) {
        try {
            commonJdbcOperations.insertReceivedMessageToTable(consumerId,
                    msgId);
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}
