package com.iris.common;

import lombok.Data;

/**
 * @Author: zfl
 * @Date: 2020/7/15 11:22
 * @Version: 1.0.0
 */
@Data
public class EventMessage {
    private long id;
    private String eventAggregateType;
    private String eventAggregateId;
    private String eventClassName;
    private String msgId;
    private String body;
    private int published;

    @Override
    public String toString() {
        return "EventMessage{" +
                "id=" + id +
                ", eventAggregateType='" + eventAggregateType + '\'' +
                ", eventAggregateId='" + eventAggregateId + '\'' +
                ", eventClassName='" + eventClassName + '\'' +
                ", msgId='" + msgId + '\'' +
                ", body='" + body + '\'' +
                ", published=" + published +
                '}';
    }
}
