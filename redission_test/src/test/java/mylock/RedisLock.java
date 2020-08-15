package mylock;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * Created by jiashen on 2020/6/28.
 */
public class RedisLock {

    private RedissonClient redisson;
    
    public RedisLock(RedissonClient r) {
        this.redisson = r;
    }

    public LockObject tryLock(long waitTime, long leaseTime, TimeUnit unit, String lockKey) throws Exception {

        final RLock lock = this.redisson.getLock(lockKey);

        boolean res =  lock.tryLock(waitTime, leaseTime, unit);

        return new LockObject(lock, res);
    }

    public void tryUnLock(LockObject lockObject) {
        if (this.redisson == null) {
            System.out.println("redisson is null");
            return;
        }

        if (lockObject == null) {
            System.out.println("lockObject is null");
            return;
        }

        if (! lockObject.lockSuccess) {
            System.out.println("lockObject.lockResult is false, do not need unlock.");
            return;
        }

        if (lockObject.getLock() == null) {
            System.out.println("lockObject.getLock() is null");
            return;
        }

        lockObject.getLock().unlock();
    }



    public static class LockObject {
        public LockObject(RLock lock, boolean lockSuccess) {
            this.lock = lock;
            this.lockSuccess = lockSuccess;
        }

        private RLock lock;

        private boolean lockSuccess;

        public RLock getLock() {
            return lock;
        }

        public void setLock(RLock lock) {
            this.lock = lock;
        }

        public boolean isLockSuccess() {
            return lockSuccess;
        }

        public void setLockSuccess(boolean lockSuccess) {
            this.lockSuccess = lockSuccess;
        }
    }
}
