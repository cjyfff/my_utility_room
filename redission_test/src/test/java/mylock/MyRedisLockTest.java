package mylock;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import mylock.RedisLock.LockObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

/**
 * Created by jiashen on 2020/6/28.
 */
public class MyRedisLockTest {

    private RedissonClient redisson;

    @Before
    public void initMethod() {
        Config config = new Config();
        config.setTransportMode(TransportMode.NIO);
        config.useSingleServer().setAddress("redis://192.168.3.106:6379");
        this.redisson = Redisson.create(config);

    }

    @Test
    public void testLock() {
        RedisLock redisLock = new RedisLock(this.redisson);

        try {

            String lockKey = "test_lock";

            ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("lock-test-pool-%d").build();
            ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 20, 10000L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(20),
                namedThreadFactory,
                (r, executor) -> {
                    try {
                        if (!executor.isShutdown()) {
                            executor.getQueue().put(r);
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });

            // 使用非线程安全的类，进行非线程安全的操作
            final Integer[] a = {0};

            AtomicInteger standardCount = new AtomicInteger(0);

            for (int i = 0; i < 10; i++) {
                pool.submit(() -> {
                    for (int j = 0; j < 100; j++) {
                        LockObject lockObject = null;
                        try {
                            lockObject = redisLock.tryLock(0, 60, TimeUnit.SECONDS, lockKey);
                            if (! lockObject.isLockSuccess()) {
                                continue;
                            }

                            a[0]++;
                            standardCount.getAndIncrement();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (lockObject != null) {
                                redisLock.tryUnLock(lockObject);
                            }
                        }
                    }
                });
            }

            pool.shutdown();

            while (!pool.isTerminated()) {
                TimeUnit.SECONDS.sleep(1);
            }

            System.out.println("a: " + a[0]);

            Assert.assertEquals(standardCount.get(), (int)a[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void afterMethod() {
        if (redisson != null) {
            redisson.shutdown();
        }
    }
}
