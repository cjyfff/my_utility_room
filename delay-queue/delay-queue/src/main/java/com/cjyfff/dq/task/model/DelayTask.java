package com.cjyfff.dq.task.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DelayTask {
    private Long id;

    private String taskId;

    private String functionName;

    private String params;

    private Integer status;

    private Integer retryInterval;

    private Byte retryCount;

    private Byte shardingId;

    private Date createdAt;

    private Date modifiedAt;
}