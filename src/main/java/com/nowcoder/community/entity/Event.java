package com.nowcoder.community.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 事件，用于消息发布
 */
public class Event {

    private String topic; //事件主题
    private int userId; //触发者
    private int entityType; //触发实体类型
    private int entityId; //触发实体id
    private int entityUserId; //触发实体的作者
    private Map<String, Object> data = new HashMap<>(); //为了后续扩容而设计的data字段

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    //设计直接传入key value 即可对map赋值，返回类型为自己，即可以一直用 . 去无限调用，更方便
    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

}
