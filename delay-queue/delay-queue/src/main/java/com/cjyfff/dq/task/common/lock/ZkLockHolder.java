package com.cjyfff.dq.task.common.lock;

import java.util.Map;

import com.google.common.collect.Maps;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.springframework.util.StringUtils;

/**
 * Created by jiashen on 2018/10/30.
 */
public class ZkLockHolder {
    private static final ThreadLocal<Map<String, InterProcessLock>> HOLDER = new ThreadLocal<>();

    /**
     * 每一个任务，在LOCK_KEY_SET中都会对应一个entity，直到该任务执行完毕才会删除这个entity，
     * 因此LOCK_KEY_SET的初始容量要设置大一点
     */
    private static final Integer LOCK_KEY_SET_INIT_SIZE = 1024;

    private ZkLockHolder() {}

    public static void setLockKeySet(String key, InterProcessLock lock) {
        if (StringUtils.isEmpty(key) || lock == null) {
            throw new IllegalArgumentException("Parameters key and lock can not be null");
        }

        if (HOLDER.get() == null) {
            // ThreadLocal是线程独有，不需要考虑Map的线程安全
            Map<String, InterProcessLock> lockKeySet = Maps.newHashMapWithExpectedSize(LOCK_KEY_SET_INIT_SIZE);
            lockKeySet.put(key, lock);
            HOLDER.set(lockKeySet);
        } else {
            HOLDER.get().put(key, lock);
        }
    }

    public static InterProcessLock getLockByKey(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("Parameters key can not be null");
        }

        if (HOLDER.get() == null) {
            // ThreadLocal是线程独有，不需要考虑线程安全
            Map<String, InterProcessLock> lockKeySet = Maps.newHashMapWithExpectedSize(LOCK_KEY_SET_INIT_SIZE);
            HOLDER.set(lockKeySet);
            return null;
        } else {
            return HOLDER.get().get(key);
        }
    }

    public static void removeLockByKey(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("Parameters key can not be null");
        }

        if (HOLDER.get() == null) {
            return;
        }

        HOLDER.get().remove(key);
    }


    public static void clearHolder() {
        HOLDER.remove();
    }
}
