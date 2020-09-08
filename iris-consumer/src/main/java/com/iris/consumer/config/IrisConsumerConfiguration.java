package com.iris.consumer.config;

import com.iris.common.jdbc.CommonJdbcOperations;
import com.iris.common.jdbc.config.CommonJdbcConfiguration;
import com.iris.consumer.DomainEventMessageConsumer;
import com.iris.consumer.DomainEventMessageConsumerImpl;
import com.iris.consumer.DomainEventMessageDispatcher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Author: zfl
 * @Date: 2020/8/18 16:48
 * @Version: 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(DomainEventMessageDispatcher.class)
@Import({CommonJdbcConfiguration.class})
public class IrisConsumerConfiguration {

    @Bean
    @ConditionalOnMissingBean(DomainEventMessageConsumer.class)
    public DomainEventMessageConsumer eventMessageConsumer(CommonJdbcOperations commonJdbcOperations) {
        return new DomainEventMessageConsumerImpl(commonJdbcOperations);
    }

    @Bean
    @ConditionalOnMissingBean(DomainEventMessageDispatcher.class)
    public DomainEventMessageDispatcher eventMessageDispatcher(DomainEventMessageConsumer eventMessageConsumer) {
        return new DomainEventMessageDispatcher(eventMessageConsumer);
    }
}
