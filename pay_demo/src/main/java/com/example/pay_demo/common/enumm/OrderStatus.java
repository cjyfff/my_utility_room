package com.example.pay_demo.common.enumm;

/**
 * Created by jiashen on 2020/9/7.
 */
public enum OrderStatus {
    INIT(0, "init"),
    SUCCESS(1, "success"),
    FAIL(2, "fail")
    ;

    private Integer status;

    private String desc;

    OrderStatus(Integer status, String desc) {
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
