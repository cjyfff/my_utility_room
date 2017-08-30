package com.example.demo.common.cache.redis;

import java.util.List;
import java.util.Set;

import com.example.demo.common.cache.Cache;
import com.example.demo.common.cache.CacheException;
import com.example.demo.common.cache.JSONSerializer;
import com.example.demo.common.cache.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 保留来做单点连接的例子
 */
@Configuration
@Component("redisCache")
public class RedisCacheSingle implements Cache {

    private Logger logger = LoggerFactory.getLogger(RedisCacheSingle.class);

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.password}")
    private String password;


    private Serializer serializer = new JSONSerializer();

    private static JedisPool jedisPool = null;

    @Override
    public Object get(String key, Class<?> clazz) throws CacheException {
        Jedis jedis = null;
        try{
            if(null == jedisPool){
                jedisPool = getPool();
            }
            jedis = jedisPool.getResource();
            return serializer.deSerialize(jedis.get(key), clazz);
        }catch ( Exception e ){
            logger.error(e.getMessage(), e);
        }finally {
            if(null != jedis){
                jedis.close();
            }
        }
        return null;
    }

    @Override
    public void set(String key, Object value) throws CacheException {
        Jedis jedis = null;
        try{
            if(null == jedisPool){
                jedisPool = getPool();
            }
            jedis = jedisPool.getResource();
            jedis.set(key, serializer.serialize(value));
        }catch ( Exception e ){
            logger.error(e.getMessage(), e);
        }finally {
            if(null != jedis){
                jedis.close();
            }
        }
    }

    @Override
    public Object lPop(String key, Class<?> clazz) throws CacheException {
        Jedis jedis = null;
        try{
            if(null == jedisPool){
                jedisPool = getPool();
            }
            jedis = jedisPool.getResource();
            return serializer.deSerialize(jedis.lpop(key), clazz);
        }catch ( Exception e ){
            logger.error(e.getMessage(), e);
        }finally {
            if(null != jedis){
                jedis.close();
            }
        }
        return null;
    }

    @Override
    public void rPush(String key, Object value) throws CacheException {
        Jedis jedis = null;
        try{
            if(null == jedisPool){
                jedisPool = getPool();
            }
            jedis = jedisPool.getResource();
            jedis.rpush(key, serializer.serialize(value));
        }catch ( Exception e ){
            logger.error(e.getMessage(), e);
            throw new CacheException("Redis缓存当前不可用");
        }finally {
            if(null != jedis){
                jedis.close();
            }
        }
    }

    @Override
    public Long lLen(String key) throws CacheException {
        Jedis jedis = null;
        try{
            if(null == jedisPool){
                jedisPool = getPool();
            }
            jedis = jedisPool.getResource();
            return jedis.llen(key);
        }catch ( Exception e ){
            logger.error(e.getMessage(), e);
            throw new CacheException("Redis缓存当前不可用");
        }finally {
            if(null != jedis){
                jedis.close();
            }
        }
    }

    @Override
    public boolean exists(String key) throws CacheException {
        Jedis jedis = null;
        try{
            if(null == jedisPool){
                jedisPool = getPool();
            }
            jedis = jedisPool.getResource();
            return jedis.exists(key);
        }catch ( Exception e ){
            logger.error(e.getMessage(), e);
            throw new CacheException("Redis缓存当前不可用");
        }finally {
            if(null != jedis){
                jedis.close();
            }
        }
    }

    @Override
    public Long expire(String key, int seconds) throws CacheException {
        Jedis jedis = null;
        try{
            if(null == jedisPool){
                jedisPool = getPool();
            }
            jedis = jedisPool.getResource();
            return jedis.expire(key, seconds);
        }catch ( Exception e ){
            logger.error(e.getMessage(), e);
            throw new CacheException("Redis缓存当前不可用");
        }finally {
            if(null != jedis){
                jedis.close();
            }
        }
    }

    @Override
    public Long setnx(String key, String value) throws CacheException {
        Jedis jedis = null;
        try{
            if(null == jedisPool){
                jedisPool = getPool();
            }
            jedis = jedisPool.getResource();
            return jedis.setnx(key, value);
        }catch ( Exception e ){
            logger.error(e.getMessage(), e);
            throw new CacheException("Redis缓存当前不可用");
        }finally {
            if(null != jedis){
                jedis.close();
            }
        }
    }

    @Override
    public Long rpush(String key, String value) throws CacheException {
        Jedis jedis = null;
        try{
            if(null == jedisPool){
                jedisPool = getPool();
            }
            jedis = jedisPool.getResource();
            return jedis.rpush(key, value);
        }catch ( Exception e ){
            logger.error(e.getMessage(), e);
            throw new CacheException("Redis缓存当前不可用");
        }finally {
            if(null != jedis){
                jedis.close();
            }
        }
    }

    @Override
    public String lpop(String key) throws CacheException {
        Jedis jedis = null;
        try{
            if(null == jedisPool){
                jedisPool = getPool();
            }
            jedis = jedisPool.getResource();
            return jedis.lpop(key);
        }catch ( Exception e ){
            logger.error(e.getMessage(), e);
            throw new CacheException("Redis缓存当前不可用");
        }finally {
            if(null != jedis){
                jedis.close();
            }
        }
    }

    @Override
    public void setex(String key, int seconds, Object value) throws CacheException {
        Jedis jedis = null;
        try{
            if(null == jedisPool){
                jedisPool = getPool();
            }
            jedis = jedisPool.getResource();
            jedis.setex(key, seconds, serializer.serialize(value));
        }catch ( Exception e ){
            logger.error(e.getMessage(), e);
            throw new CacheException("Redis缓存当前不可用");
        }finally {
            if(null != jedis){
                jedis.close();
            }
        }
    }

    @Override
    public Long incr(String key) throws CacheException {
        Jedis jedis = null;
        try{
            if(null == jedisPool){
                jedisPool = getPool();
            }
            jedis = jedisPool.getResource();
            return jedis.incr(key);
        }catch ( Exception e ){
            logger.error(e.getMessage(), e);
            throw new CacheException("Redis缓存当前不可用");
        }finally {
            if(null != jedis){
                jedis.close();
            }
        }
    }

    @Override
    public void evict(String key) throws CacheException {
    }

    @Override
    public void evict(List<?> keys) throws CacheException {

    }

    @Override
    public Set<String> keys(String pattern) throws CacheException {
        Jedis jedis = null;
        try{
            if(null != jedisPool){
                jedisPool = getPool();
            }
            jedis = jedisPool.getResource();
            return jedis.hkeys(pattern);
        }catch ( Exception e ){
            logger.error(e.getMessage(), e);
            throw new CacheException("Redis缓存当前不可用");
        }finally {
            if(null != jedis){
                jedis.close();
            }
        }
    }

    @Override
    public void clear(String key) throws CacheException {
        Jedis jedis = null;
        try{
            if(null == jedisPool){
                jedisPool = getPool();
            }
            jedis = jedisPool.getResource();
            jedis.del(key);
        }catch ( Exception e ){
            logger.error(e.getMessage(), e);
            throw new CacheException("Redis缓存当前不可用");
        }finally {
            if(null != jedis){
                jedis.close();
            }
        }
    }

    @Override
    public void destroy() throws CacheException {

    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Bean
    public JedisPool getPool() throws CacheException {
        try {

            if (jedisPool == null) {
//                Assert.checkNonNull(host);
//                Assert.checkNonNull(port);
//                Assert.checkNonNull(timeout);
//                Assert.checkNonNull(password);
//                Assert.checkNonNull(maxIdle);
                logger.info("Initializing Redis single with '{}' ...", host+":"+port);
                JedisPoolConfig config = new JedisPoolConfig();
                //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
                config.setMaxIdle(maxIdle);
                config.setTestOnBorrow(true);
                config.setTestOnReturn(true);
                //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
                config.setTestOnBorrow(true);
                jedisPool = new JedisPool(config, host, port, timeout, password);
                logger.info("Redis single connected successfully! host:port={}", host+":"+port);
            }
        }catch ( Exception e ){
            throw new CacheException("Please check if  was configured properly!", e);
        }
        return jedisPool;
    }
}
