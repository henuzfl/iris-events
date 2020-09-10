package com.iris.reader.config;

import com.iris.common.jdbc.CommonJdbcOperations;
import com.iris.common.jdbc.config.CommonJdbcConfiguration;
import com.iris.producer.IDomainEventProducer;
import com.iris.producer.config.IrisProducerConfiguration;
import com.iris.reader.DomainEventReaderLeadership;
import com.iris.reader.DomainEventReaderServiceImpl;
import com.iris.reader.IDomainEventReaderService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Author: zfl
 * @Date: 2020/9/8 17:35
 * @Version: 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@Import({CommonJdbcConfiguration.class, IrisProducerConfiguration.class})
@ConditionalOnClass(IrisReaderConfiguration.class)
@EnableConfigurationProperties(IrisReaderProperties.class)
@EnableAutoConfiguration
public class IrisReaderConfiguration {

    @Bean
    @ConditionalOnMissingBean(IDomainEventReaderService.class)
    public IDomainEventReaderService domainEventReader(CommonJdbcOperations commonJdbcOperations, IDomainEventProducer domainEventProducer) {
        return new DomainEventReaderServiceImpl(commonJdbcOperations,
                domainEventProducer);
    }

    @Bean
    @ConditionalOnMissingBean(DomainEventReaderLeadership.class)
    public DomainEventReaderLeadership domainEventReaderLeadership(IrisReaderProperties irisReaderProperties, IDomainEventReaderService domainEventReader) {
        return new DomainEventReaderLeadership(irisReaderProperties.getZookeeper().getConnectString(),
                domainEventReader);
    }

}
