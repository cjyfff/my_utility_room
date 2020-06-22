import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

/**
 * Created by jiashen on 19-1-16.
 */
public class RedissionTest {

    private RedissonClient redisson;

    @Before
    public void initMethod() {
        Config config = new Config();
        config.setTransportMode(TransportMode.NIO);
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        redisson = Redisson.create(config);
    }

    @Test
    public void testAtom() {
        // 僅有此語句，而沒有執行getAndIncrement的話，redis中實際上沒有保存該值
        RAtomicLong long1 = redisson.getAtomicLong("test_long1");
        long1.expire(60, TimeUnit.SECONDS);
        // 沒設值或者值已經過期時，執行get()是返回0
        System.out.println(long1.get());
        long1.getAndIncrement();
        System.out.println(long1.get());
    }

    @Test
    public void testLock0() throws Exception {

        try {
            final RLock lock = redisson.getLock("anyLock0");

            boolean res = lock.tryLock(100, 100, TimeUnit.SECONDS);

            Assert.assertTrue(res);

            boolean res1 = lock.tryLock(100, 100, TimeUnit.SECONDS);

            // 测试可重入
            Assert.assertTrue(res1);

            lock.unlock();
            lock.unlock();
            // 解多次锁不会报错
            lock.unlock();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        System.out.println("END");
    }

    @Test
    public void testLock1() throws Exception {

        try {
            final RLock lock = redisson.getLock("anyLock1");

            boolean res = lock.tryLock(100, 100, TimeUnit.SECONDS);

            Assert.assertTrue(res);

            Thread t1 = new Thread(new Runnable() {
                public void run() {
                    try {
                        boolean res1 = lock.tryLock(0, 100, TimeUnit.SECONDS);
                        // 另一个线程无法获取到锁
                        Assert.assertFalse(res1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            t1.start();
            t1.join();

            lock.unlock();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        System.out.println("END");
    }

    @Test
    public void testLock2() throws Exception {

        try {
            final RLock lock = redisson.getLock("anyLock2");

            // 加两次锁
            boolean res = lock.tryLock(0, 100, TimeUnit.SECONDS);

            boolean res1 = lock.tryLock(0, 100, TimeUnit.SECONDS);

            Assert.assertTrue(res);
            Assert.assertTrue(res1);
            // 只解锁一次
            lock.unlock();

            Thread t1 = new Thread(new Runnable() {
                public void run() {
                    try {
                        boolean res2 = lock.tryLock(0, 100, TimeUnit.SECONDS);
                        // 另一个线程还是无法获取到锁
                        Assert.assertFalse(res2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            t1.start();
            t1.join();

            lock.unlock();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        System.out.println("END");
    }

    @Test
    public void testLock3() throws Exception {

        try {
            final RLock lock = redisson.getLock("anyLock3");

            // 加两次锁
            boolean res = lock.tryLock(0, 100, TimeUnit.SECONDS);

            boolean res1 = lock.tryLock(0, 100, TimeUnit.SECONDS);

            Assert.assertTrue(res);
            Assert.assertTrue(res1);
            // 解锁两次
            lock.unlock();
            lock.unlock();

            Thread t1 = new Thread(new Runnable() {
                public void run() {
                    try {
                        final RLock lock2 = redisson.getLock("anyLock3");
                        boolean res2 = lock2.tryLock(0, 100, TimeUnit.SECONDS);
                        // 另一个线程可以获取到锁
                        Assert.assertTrue(res2);
                        lock2.unlock();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            t1.start();
            t1.join();



        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        System.out.println("END");
    }

    @Test
    public void testSemaphore() throws Exception {

        try {
            RSemaphore semaphore = redisson.getSemaphore("semaphore");

            semaphore.release();

            Thread t1 = new Thread(new Runnable() {
                public void run() {
                    try {
                        RSemaphore semaphore = redisson.getSemaphore("semaphore");
                        boolean res = semaphore.tryAcquire(10, TimeUnit.SECONDS);
                        Assert.assertTrue(res);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            t1.start();
            t1.join();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        System.out.println("END");
    }

    @After
    public void afterMethod() {
        if (redisson != null) {
            redisson.shutdown();
        }
    }
}
