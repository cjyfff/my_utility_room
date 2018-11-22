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
     * 接收对方的taskId 加上一个随机数作为保存到数据库的task id
     * 以防调用方task id重复
     */
    @NotEmpty(message = "can not be empty")
    private String taskId;

    @NotEmpty(message = "can not be empty")
    private String functionName;

    @NotNull(message = "can not be null")
    private Long delayTime;

    @NotNull(message = "can not be null")
    private String nonceStr;

    private String params;

    private Byte retryCount;

    private Integer retryInterval;
}
