package com.cjyfff.dq.task.common;


/**
 * Created by jiashen on 2018/9/24.
 */
public enum TaskStatus {
    ACCEPT(0, "已接收"),
    PROCESSING(100, "执行中"),
    TRANSMITING(101, ""),
    RETRYING(150, ""),
    PROCESS_SUCCESS(200, ""),
    PROCESS_FAIL(400, ""),
    RETRY_FAIL(500, "");

    private Integer status;

    private String desc;

    TaskStatus(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
