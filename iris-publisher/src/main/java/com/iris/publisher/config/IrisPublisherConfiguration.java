package com.iris.publisher.config;

import com.iris.common.jdbc.CommonJdbcOperations;
import com.iris.common.jdbc.config.CommonJdbcConfiguration;
import com.iris.publisher.DomainEventPublisherImpl;
import com.iris.publisher.IDomainEventPublisher;
import com.iris.publisher.annotation.DomainEventAspect;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Author: zfl
 * @Date: 2020/7/13 16:56
 * @Version: 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@Import({CommonJdbcConfiguration.class})
@ConditionalOnClass(IrisPublisherConfiguration.class)
@EnableAutoConfiguration
public class IrisPublisherConfiguration {

    @Bean
    @ConditionalOnMissingBean(IDomainEventPublisher.class)
    public IDomainEventPublisher domainEventPublisher(CommonJdbcOperations commonJdbcOperations) {
        return new DomainEventPublisherImpl(commonJdbcOperations);
    }

    @Bean
    @ConditionalOnMissingBean(DomainEventAspect.class)
    public DomainEventAspect domainEventAspect() {
        return new DomainEventAspect();
    }
}
