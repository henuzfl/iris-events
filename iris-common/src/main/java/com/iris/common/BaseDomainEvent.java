package com.iris.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.Date;
import java.util.UUID;

/**
 * 领域事件基类
 *
 * @Author: zfl
 * @Date: 2020/7/13 15:56
 * @Version: 1.0.0
 */
@Getter
public abstract class BaseDomainEvent{
    @JsonIgnore
    private final String id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private final Date createdTime;

    @JsonIgnore
    private final String eventClassName;

    @JsonIgnore
    private String aggregateId;

    @JsonIgnore
    private String aggregateType;

    public BaseDomainEvent(BaseAggregateRoot aggregateRoot) {
        this.id = UUID.randomUUID().toString();
        this.createdTime = new Date();
        this.eventClassName = this.getClass().getSimpleName();
        this.aggregateId = String.valueOf(aggregateRoot.getId());
        this.aggregateType = aggregateRoot.getClass().getName();
    }

    public BaseDomainEvent() {
        this.id = UUID.randomUUID().toString();
        this.createdTime = new Date();
        this.eventClassName = this.getClass().getSimpleName();
    }
}
