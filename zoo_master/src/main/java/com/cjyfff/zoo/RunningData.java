package com.cjyfff.zoo;

import java.io.Serializable;

/**
 * Created by jiashen on 2018/8/5.
 */
public class RunningData implements Serializable {

    private static final long serialVersionUID = 4260577459043203630L;

    /**
     * 服务器id
     */
    private long cid;

    /**
     * 服务器名称
     */
    private String name;

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
