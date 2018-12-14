package com.cjyfff.dq.task.common.lock;

import java.util.Map;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

/**
 * Created by jiashen on 18-12-14.
 */
public class UnlockAfterDbCommitInfoHolder {
    private static final ThreadLocal<Map<String, UnlockAfterDbCommitInfo>> HOLDER = new ThreadLocal<>();

    /**
     * 每一个任务，在LOCK_KEY_SET中都会对应一个entity，直到该任务执行完毕才会删除这个entity，
     * 因此LOCK_KEY_SET的初始容量要设置大一点
     */
    private static final Integer INFO_MAP_INIT_SIZE = 1024;

    public static void setInfo2Holder(String key, boolean needUnlock) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("Parameters key and lock can not be null");
        }

        Map<String, UnlockAfterDbCommitInfo> infoMap;
        if (HOLDER.get() == null) {
            infoMap = Maps.newHashMapWithExpectedSize(INFO_MAP_INIT_SIZE);
        } else {
            infoMap = HOLDER.get();
        }

        UnlockAfterDbCommitInfo info = new UnlockAfterDbCommitInfo();
        info.setNeedUnlock(needUnlock);
        infoMap.put(key, info);
    }

    public static void setInfo2Holder(String key) {
        setInfo2Holder(key, true);
    }

    public static UnlockAfterDbCommitInfo getInfoByKey(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("Parameters key and lock can not be null");
        }

        if (HOLDER.get() == null) {
            Map<String, UnlockAfterDbCommitInfo> infoMap = Maps.newHashMapWithExpectedSize(INFO_MAP_INIT_SIZE);
            HOLDER.set(infoMap);
            return null;
        }

        return HOLDER.get().get(key);
    }

    public static Map<String, UnlockAfterDbCommitInfo> getAllInfoMap() {
        if (HOLDER.get() == null) {
            Map<String, UnlockAfterDbCommitInfo> infoMap = Maps.newHashMapWithExpectedSize(INFO_MAP_INIT_SIZE);
            HOLDER.set(infoMap);
            return null;
        }
        return HOLDER.get();
    }

    public static void removeInfoByKey(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("Parameters key and lock can not be null");
        }
        if (HOLDER.get() == null) {
            return;
        }

        HOLDER.get().remove(key);
    }

    @Getter
    @Setter
    public static class UnlockAfterDbCommitInfo {
        private boolean needUnlock = true;
    }
}
