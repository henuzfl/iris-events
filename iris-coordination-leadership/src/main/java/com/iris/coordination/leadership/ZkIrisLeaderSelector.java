package com.iris.coordination.leadership;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.CancelLeadershipException;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: zfl
 * @Date: 2020/8/25 9:57
 * @Version: 1.0.0
 */
public class ZkIrisLeaderSelector extends LeaderSelectorListenerAdapter implements IrisLeaderSelector, LeaderSelectorListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final CuratorFramework client;
    private String path;
    private String name;

    private final LeaderSelector leaderSelector;
    private final Runnable leaderSelectedCallback;
    private final Runnable leaderRemovedCallback;

    public ZkIrisLeaderSelector(String zkConnectString, String path,
                                String name, Runnable leaderSelectedCallback
            , Runnable leaderRemovedCallback) {
        this.client = CuratorFrameworkFactory.newClient(zkConnectString,
                new ExponentialBackoffRetry(1000,
                        Integer.MAX_VALUE));
        this.path = path;
        this.name = name;
        this.leaderSelectedCallback = leaderSelectedCallback;
        this.leaderRemovedCallback = leaderRemovedCallback;
        leaderSelector = new LeaderSelector(client, path, this);
        leaderSelector.autoRequeue();
    }

    @Override
    public void start() {
        logger.info("starting leader selector");
        this.client.start();
        leaderSelector.start();
    }

    @Override
    public void stop() {
        logger.info("Closing leader selector, name : {}", this.name);
        leaderSelector.close();
        logger.info("Closed leader selector, name : {}", this.name);
    }

    @Override
    public void takeLeadership(CuratorFramework client) {
        CountDownLatch stopCountDownLatch = new CountDownLatch(1);
        try {
            logger.info("Calling leaderSelectedCallback, leaderId : {}", name);
            this.leaderSelectedCallback.run();
            logger.info("Called leaderSelectedCallback, leaderId : {}", name);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.info("Calling leaderRemovedCallback, leaderId : {}", name);
            leaderRemovedCallback.run();
            logger.info("Called leaderRemovedCallback, leaderId : {}", name);
            return;
        }
        try {
            stopCountDownLatch.await();
        } catch (InterruptedException e) {
            logger.error("Leadership interrupted", e);
        }
        logger.info("Calling leaderRemovedCallback, leaderId : {}", name);
        leaderRemovedCallback.run();
        logger.info("Called leaderRemovedCallback, leaderId : {}", name);
    }

    @Override
    public void stateChanged(CuratorFramework curatorFramework,
                             ConnectionState connectionState) {
        logger.info("StateChanged, state : {}, leaderId : {}", connectionState,
                this.name);
        if (connectionState == ConnectionState.SUSPENDED || connectionState == ConnectionState.LOST) {
            throw new CancelLeadershipException();
        }
    }
}
