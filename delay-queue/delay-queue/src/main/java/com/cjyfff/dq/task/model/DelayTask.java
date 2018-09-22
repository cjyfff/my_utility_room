package com.cjyfff.dq.task.model;

import java.util.Date;

public class DelayTask {
    private Long id;

    private String taskId;

    private String functionName;

    private String params;

    private Byte status;

    private Integer retryInterval;

    private Byte retryCount;

    private Byte shardingId;

    private Date createdAt;

    private Date modifiedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Integer getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(Integer retryInterval) {
        this.retryInterval = retryInterval;
    }

    public Byte getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Byte retryCount) {
        this.retryCount = retryCount;
    }

    public Byte getShardingId() {
        return shardingId;
    }

    public void setShardingId(Byte shardingId) {
        this.shardingId = shardingId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}