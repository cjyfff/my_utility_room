package com.cjyfff.dq.task.vo.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jiashen on 18-10-9.
 */
@Getter
@Setter
public class InnerMsgDto {
    private String taskId;

    private String functionName;

    private Long delayTime;

    private String params;

    private Byte retryCount;

    private Integer retryInterval;
}
