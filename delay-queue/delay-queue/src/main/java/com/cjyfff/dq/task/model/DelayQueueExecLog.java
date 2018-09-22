package com.cjyfff.dq.task.model;

import java.util.Date;

public class DelayQueueExecLog {
    private Long id;

    private String taskId;

    private Byte status;

    private Byte sharding;

    private String functionName;

    private String params;

    private Long taskResultId;

    private Date createdAt;

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

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Byte getSharding() {
        return sharding;
    }

    public void setSharding(Byte sharding) {
        this.sharding = sharding;
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

    public Long getTaskResultId() {
        return taskResultId;
    }

    public void setTaskResultId(Long taskResultId) {
        this.taskResultId = taskResultId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}