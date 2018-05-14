package com.chandler.distributed.lock.zk;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by chandlerzhao on 2018/5/11.
 */
public class SimpleDistributedLockMutex extends BasicDistributedLock implements DistributedLock {

    /*用于保存Zookeeper中实现分布式锁的节点，如名称为locker：/locker，

    *该节点应该是持久节点，在该节点下面创建临时顺序节点来实现分布式锁 */
    private final String basePath;

    /*锁名称前缀，locker下创建的顺序节点例如都以lock-开头，这样便于过滤无关节点

   *这样创建后的节点类似：lock-00000001，lock-000000002*/
    private static final String LOCK_NAME = "lock-";

    private String ourLockPath;

    public SimpleDistributedLockMutex(ZooKeeper client, String basePath) {
        super(client, basePath, LOCK_NAME);
        this.basePath = basePath;
    }

    private boolean internalLock(long time, TimeUnit unit) throws Exception {
//        ourLockPath = attemptLock(time, unit);
        return ourLockPath != null;
    }

    @Override
    public void acquire() throws Exception {
        if (!internalLock(-1, null)) {
            throw new IOException("连接丢失！在路径：" + basePath + "下不能获取锁！");
        }
    }

    @Override
    public boolean acquire(long time, TimeUnit unit) throws Exception {
        return internalLock(time, unit);
    }

    @Override
    public void release() throws Exception {
//        releaseLock(ourLockPath);
    }

}
