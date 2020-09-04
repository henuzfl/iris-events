package com.iris.reader;

import com.iris.coordination.leadership.IrisLeaderSelector;
import com.iris.coordination.leadership.ZkIrisLeaderSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.UUID;

/**
 * @Author: zfl
 * @Date: 2020/8/25 11:13
 * @Version: 1.0.0
 */
public class DomainEventReaderLeadership {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String ZK_PATH = "/ancun/event/";
    private final String zkConnectString;
    private final String leaderId;
    private volatile boolean leader;
    private IDomainEventReader domainEventReader;

    @Value("${spring.application.name}")
    private String applicationName;


    public DomainEventReaderLeadership(String zkConnectString,
                                       IDomainEventReader domainEventReader) {
        this.zkConnectString = zkConnectString;
        this.leaderId = UUID.randomUUID().toString();
        this.domainEventReader = domainEventReader;
    }

    @PostConstruct
    public void init() {
        logger.info("starting message polling leadership");
        IrisLeaderSelector leaderSelector =
                new ZkIrisLeaderSelector(this.zkConnectString,
                        ZK_PATH + applicationName,
                        this.leaderId,
                        this::leaderSelectedCallback,
                        this::leaderRemovedCallback);
        leaderSelector.start();
    }

    private void leaderSelectedCallback() {
        logger.info("Assigning leadership");
        leader = true;
        domainEventReader.start();
        logger.info("Assigned leadership");
    }

    private void leaderRemovedCallback() {
        logger.info("Resigning leadership");
        leader = false;
        domainEventReader.stop();
        logger.info("Resigned leadership");
    }
}
