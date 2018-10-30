package com.cjyfff.dq.task.common.lock;

/**
 * Created by jiashen on 2018/10/30.
 */
public interface ZKLock {

    /**
     * 尝试获得锁，成功返回true，失败返回false，seconds参数代表尝试获得锁所阻塞等待的时间
     * @param key 锁key
     * @param seconds 尝试获得锁所阻塞等待的时间，单位秒
     * @return
     * @throws LockException
     */
    boolean tryLock(String key, Integer seconds) throws LockException;

    /**
     * 用于幂等的锁，不会阻塞等待，获取锁的结果立即返回
     * @param key 锁key
     * @return
     * @throws LockException
     */
    boolean idempotentLock(String key) throws LockException;

    /**
     * 尝试解锁，即使key所对应的锁不存在也不抛出异常
     * @param key 锁key
     */
    void tryUnlock(String key);
}
