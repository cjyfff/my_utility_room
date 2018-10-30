package com.cjyfff.dq.task.common.lock;

import org.apache.curator.framework.recipes.locks.InterProcessLock;

/**
 * Created by jiashen on 2018/10/30.
 */
public class ZkLockHolder {
    private static final ThreadLocal<InterProcessLock> HOLDER = new ThreadLocal<>();

    private ZkLockHolder() {}

    public static void setHolder(InterProcessLock lock) {
        HOLDER.set(lock);
    }

    public static InterProcessLock getHolder() {
        return HOLDER.get();
    }

    public static void clearHolder() {
        HOLDER.remove();
    }
}
