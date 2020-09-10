package com.iris.producer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: zfl
 * @Date: 2020/9/10 18:06
 * @Version: 1.0.0
 */
@ConfigurationProperties(prefix = "iris.kafka")
public class IrisKafkaProducerProperties {

    private List<String> bootstrapServers =
            new ArrayList<>(Collections.singletonList("localhost:9092"));

    public List<String> getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(List<String> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }
}
