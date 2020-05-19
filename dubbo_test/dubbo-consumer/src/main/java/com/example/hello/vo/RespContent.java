package com.example.hello.vo;

import java.io.Serializable;

/**
 * Created by jiashen on 2020/5/19.
 */
public class RespContent implements Serializable {
    private String code;

    private String msg;

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
