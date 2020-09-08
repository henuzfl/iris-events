package com.iris.reader;

import com.iris.common.EventMessage;
import com.iris.common.jdbc.CommonJdbcOperations;
import com.iris.producer.IDomainEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zfl
 * @Date: 2020/9/4 15:37
 * @Version: 1.0.0
 */
public class DomainEventReaderServiceImpl implements IDomainEventReaderService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final ScheduledExecutorService scheduledExecutorService;

    private final CommonJdbcOperations commonJdbcOperations;

    private final IDomainEventProducer domainEventProducer;

    public DomainEventReaderServiceImpl(CommonJdbcOperations commonJdbcOperations, IDomainEventProducer domainEventProducer) {
        this.commonJdbcOperations = commonJdbcOperations;
        this.scheduledExecutorService =
                Executors.newSingleThreadScheduledExecutor();
        this.domainEventProducer = domainEventProducer;
    }

    @Override
    public void start() {
        scheduledExecutorService.scheduleAtFixedRate(() ->
                        commonJdbcOperations.queryUnPublishedEventMessages()
                                .forEach(this::eventMessageProcess), 0,
                1, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        scheduledExecutorService.shutdownNow();
    }

    private void eventMessageProcess(EventMessage eventMessage) {
        this.domainEventProducer.processEvent(eventMessage);
    }
}
