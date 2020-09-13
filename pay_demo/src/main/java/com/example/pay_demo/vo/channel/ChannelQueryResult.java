package com.example.pay_demo.vo.channel;

/**
 * Created by jiashen on 2020/9/9.
 */
public class ChannelQueryResult {
    /**
     * 订单状态
     */
    private boolean orderSuccess;

    /**
     * 是否需要冲正
     */
    private boolean needCancel;

    private String code;

    private String msg;

    public ChannelQueryResult(boolean orderSuccess, boolean needCancel, String code, String msg) {
        this.orderSuccess = orderSuccess;
        this.needCancel = needCancel;
        this.code = code;
        this.msg = msg;
    }

    public boolean isOrderSuccess() {
        return orderSuccess;
    }

    public void setOrderSuccess(boolean orderSuccess) {
        this.orderSuccess = orderSuccess;
    }

    public boolean isNeedCancel() {
        return needCancel;
    }

    public void setNeedCancel(boolean needCancel) {
        this.needCancel = needCancel;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
