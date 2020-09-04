package com.iris.reader;

import com.iris.common.EventMessage;
import com.iris.common.jdbc.CommonJdbcOperations;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zfl
 * @Date: 2020/9/4 15:37
 * @Version: 1.0.0
 */
public class DomainEventReaderMysqlPolling implements IDomainEventReader {

    private final ScheduledExecutorService scheduledExecutorService;

    private CommonJdbcOperations commonJdbcOperations;

    public DomainEventReaderMysqlPolling(CommonJdbcOperations commonJdbcOperations) {
        this.commonJdbcOperations = commonJdbcOperations;
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
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

    }
}
