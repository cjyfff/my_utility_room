package com.example.demo.common.cache;

public class CacheException extends Exception {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    public CacheException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CacheException(String msg) {
        super(msg);
    }

}
