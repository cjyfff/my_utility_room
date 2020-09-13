package com.example.pay_demo.common.enumm;

/**
 * Created by jiashen on 2020/9/8.
 */
public enum OrderCancelStatus {
    NOT_CANCELED(0, "not canceled"),
    CANCELED(1, "success")
    ;

    OrderCancelStatus(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    private Integer status;

    private String desc;

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
