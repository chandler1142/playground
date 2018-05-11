package com.chandler.distributed.lock.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by chandlerzhao on 2018/5/11.
 */
public class RedisToolTest {

    private static JedisPool jedisPool = null;

    int n = 500;

    private final String lockKey = "minis_n";

    static {
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(200);

        config.setMaxIdle(8);

        config.setMaxWaitMillis(1000 * 100);

        config.setTestOnBorrow(true);

        jedisPool = new JedisPool(config, "127.0.0.1", 6379, 3000);

    }

    public boolean seckKill() {
        Jedis jedis = jedisPool.getResource();
        boolean result = RedisTool.tryGetDistributedLock(
                jedis,
                lockKey,
                Thread.currentThread().getName(),
                1000);
        if (result) {
            System.out.println(Thread.currentThread().getName() + "获得了锁");
            --n;
            System.out.println(Thread.currentThread().getName() + "操作完以后n: " + n);
            RedisTool.releaseDistributedLock(jedis, lockKey, Thread.currentThread().getName());
            jedis.close();
            return true;
        } else {
            System.out.println(Thread.currentThread().getName() + "没有获取到锁呢");
            return false;
        }
    }

    public static class ThreadA extends Thread {

        private RedisToolTest service;

        public ThreadA(RedisToolTest service) {
            this.service = service;
        }

        @Override
        public void run() {
            while(!service.seckKill());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        RedisToolTest redisToolTest = new RedisToolTest();
        for (int i = 0; i < 500; i++) {
            ThreadA threadA = new ThreadA(redisToolTest);
            threadA.start();
        }

        System.out.println(redisToolTest.n);
    }
}
