package com.cjyfff.dq.task.vo.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jiashen on 2018/9/23.
 */
@Getter
@Setter
public class AcceptMsgDto {

    /**
     * taskId，需要保证唯一
     */
    @NotEmpty(message = "can not be empty")
    private String taskId;

    @NotEmpty(message = "can not be empty")
    private String functionName;

    @NotNull(message = "can not be null")
    private Long delayTime;

    /**
     * 随机字符串，用于保证接口幂等
     */
    @NotNull(message = "can not be null")
    private String nonceStr;

    private String params;

    private Byte retryCount;

    private Integer retryInterval;
}
