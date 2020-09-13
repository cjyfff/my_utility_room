package com.example.pay_demo.common;

import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2020/9/7.
 */
@Component
public class Lock {
    public LockResult tryLock(String lockKey) {
        return new LockResult(true);
    }

    public void tryUnLock(LockResult lockResult) {
        if (lockResult == null || ! lockResult.isSuccess) {
            return;
        }
    }


    public static class LockResult {
        public LockResult(boolean isSuccess) {
            this.isSuccess = isSuccess;
        }

        private boolean isSuccess;

        public boolean isSuccess() {
            return isSuccess;
        }

        public void setSuccess(boolean success) {
            isSuccess = success;
        }
    }
}
