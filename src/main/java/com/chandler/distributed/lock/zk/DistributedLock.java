package com.chandler.distributed.lock.zk;

import java.util.concurrent.TimeUnit;

/**
 * Created by chandlerzhao on 2018/5/11.
 */
public interface DistributedLock {

    public void acquire() throws Exception;

    public boolean acquire(long time, TimeUnit unit) throws Exception;

    public void release() throws Exception;

}
