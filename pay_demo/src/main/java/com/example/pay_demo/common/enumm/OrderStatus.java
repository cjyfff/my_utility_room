package com.example.pay_demo.common.enumm;

/**
 * Created by jiashen on 2020/9/7.
 */
public enum OrderStatus {
    INIT(100, "init"),
    CHANNEL_PAYING(101, "正在调用渠道支付"),
    SUCCESS(800, "success"),
    FAIL(900, "fail")
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
