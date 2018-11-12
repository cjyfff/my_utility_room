package com.cjyfff.dq.task.common.lock;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by jiashen on 2018/10/30.
 */
@Component
@Slf4j
public class ZkLockImpl implements ZkLock {

    private final static String LOCK_PATH = "/task_lock";

    @Override
    public boolean tryLock(CuratorFramework client, String key, Integer seconds) throws LockException {

        try {
            if (StringUtils.isEmpty(key)) {
                return false;
            }

            String keyLockPath = getKeyLockPach(key);
            InterProcessLock lock = new InterProcessMutex(client, keyLockPath);
            // 加锁成功后需要把锁对象存入TreadLocal，加锁时根据锁对象来解锁，防止对别的线程
            // 所加的锁进行解锁操作
            if (lock.acquire(seconds, TimeUnit.SECONDS)) {
                ZkLockHolder.setHolder(lock);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("ZkLock tryLock method get error.", e);
            // 此处不能return false;，否则的话出现异常，调用方会误以为是加锁失败
            throw new LockException(e.getMessage());
        }
    }

    @Override
    public boolean idempotentLock(CuratorFramework client, String key) throws LockException {
        return this.tryLock(client, key, 0);
    }

    @Override
    public InterProcessLock getLockInstance() {
        return ZkLockHolder.getHolder();
    }

    @Override
    public void tryUnlock(InterProcessLock lock) {
        try {

            if (lock == null) {
                return;
            }

            lock.release();

            ZkLockHolder.clearHolder();
        } catch (Exception e) {
            log.error("ZkLock tryUnlock method get error.", e);
        }
    }

    private String getKeyLockPach(String key) {
        return LOCK_PATH + "/" + key;
    }
}
