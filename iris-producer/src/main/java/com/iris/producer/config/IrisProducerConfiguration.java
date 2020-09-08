package com.iris.producer.config;

import com.iris.common.jdbc.CommonJdbcOperations;
import com.iris.common.jdbc.config.CommonJdbcConfiguration;
import com.iris.producer.IDomainEventProducer;
import com.iris.producer.impl.DomainEventKafkaProducer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Author: zfl
 * @Date: 2020/9/8 18:04
 * @Version: 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@Import({CommonJdbcConfiguration.class})
@ConditionalOnClass(IrisProducerConfiguration.class)
@EnableAutoConfiguration
public class IrisProducerConfiguration {

    @Bean
    @ConditionalOnMissingBean(IDomainEventProducer.class)
    public IDomainEventProducer domainEventProducer(CommonJdbcOperations commonJdbcOperations) {
        return new DomainEventKafkaProducer(commonJdbcOperations);
    }
}
