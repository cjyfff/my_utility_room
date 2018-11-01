package com.cjyfff.dq.task.vo.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jiashen on 18-10-9.
 */
@Getter
@Setter
public class InnerMsgDto {

    @NotEmpty(message = "can not be empty")
    private String taskId;

    @NotEmpty(message = "can not be empty")
    private String functionName;

    @NotNull(message = "can not be null")
    private Long delayTime;

    private String params;

    private Byte retryCount;

    private Integer retryInterval;
}
