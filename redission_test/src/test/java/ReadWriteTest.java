import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

/**
 * Created by jiashen on 19-1-17.
 */
public class ReadWriteTest {

    private Map<String, String> db = new ConcurrentHashMap<>();

    private RedissonClient redisson;

    private final static Long SHORT_EXPIRE = 30L;

    private final static Long LONG_EXPIRE = 60L;

    @Before
    public void initMethod() {
        Config config = new Config();
        config.setTransportMode(TransportMode.NIO);
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        redisson = Redisson.create(config);
    }

    @Test
    public void test() {

        //updateData("AAA", "123");
        //getData("AAA");

        ConcurrentSkipListSet skipListSet = new ConcurrentSkipListSet();

        String myKey = "AAA";

        ThreadFactory rNamedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("read-pool-%d").build();
        ThreadPoolExecutor readPool = new ThreadPoolExecutor(100, 200, 10000L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1000),
            rNamedThreadFactory,
            (r, executor) -> {
                try {
                    if (!executor.isShutdown()) {
                        executor.getQueue().put(r);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

        ThreadFactory wNamedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("write-pool-%d").build();
        ThreadPoolExecutor writePool = new ThreadPoolExecutor(100, 200, 10000L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1000),
            wNamedThreadFactory,
            (r, executor) -> {
                try {
                    if (!executor.isShutdown()) {
                        executor.getQueue().put(r);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

        for (int i = 0; i < 1000; i++) {
            readPool.submit(()-> getData(myKey));
        }

        for (int i = 0; i < 1000; i++) {
            writePool.submit(()->{
                long time = System.currentTimeMillis();
                skipListSet.add(time);
                System.out.println("write data: " + time);
                updateData(myKey, Long.valueOf(time).toString());
            });
        }

        readPool.shutdown();
        writePool.shutdown();

        while (! (readPool.isTerminated() && writePool.isTerminated())) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("skipListSet last: " + skipListSet.last());
        System.out.println("get Data: " + getData(myKey));

        Assert.assertEquals((Long)skipListSet.last(), Long.valueOf(getData(myKey)));

        System.out.println("END");
    }

    @After
    public void afterMethod() {
        if (redisson != null) {
            redisson.shutdown();
        }
    }

    private String getData(String key) {
        RBucket<String> bucket = redisson.getBucket(key);

        String value = bucket.get();

        // 缓存中的数据为null时才查库，为空字符串时直接返回
        if (value == null) {
            value = db.get(key);

            if (value == null) {
                // 一个查询返回的数据为null，不管是数据不存在，还是该数据真的是空值，我们仍然把这个空结果进行缓存，
                // 但它的过期时间应该设置得很短，这样防止恶意查询空数据导致缓存穿透
                bucket.set(value, SHORT_EXPIRE + (int)(1 + Math.random() * 10), TimeUnit.SECONDS);
            } else {
                // 过期时间加上随机数
                bucket.set(value, LONG_EXPIRE + (int)(1 + Math.random() * 10), TimeUnit.SECONDS);
            }
        }

        return value;
    }

    private void updateData(String key, String value) {

        updateCacheAndDb(() -> db.put(key, value), key);

    }

    private void updateCacheAndDb(IUpdateData updateData, String key) {
        final String key1 = key;
        // 先删除缓存
        // =============这段时间内的读取请求产生脏数据到缓存================
        RBucket<String> bucket = redisson.getBucket(key1);
        bucket.delete();

        updateData.run();

        // ===============数据正式提交后读才是安全的======================

        // 异步双删除
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(8);
                RBucket<String> bucket1 = redisson.getBucket(key1);
                bucket1.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
