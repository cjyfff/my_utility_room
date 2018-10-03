package com.cjyfff.dq.task.vo.dto;

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
    private String taskId;

    private String functionName;

    private Long delayTime;

    private String params;

    private Byte retryCount;

    private Integer retryInterval;
}
