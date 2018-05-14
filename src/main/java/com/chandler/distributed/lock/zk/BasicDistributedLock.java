package com.chandler.distributed.lock.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

/**
 * Created by chandlerzhao on 2018/5/11.
 *
 * https://blog.csdn.net/sunfeizhi/article/details/51926396
 */
public class BasicDistributedLock {

    private final ZooKeeper client;
    private final String path;
    private final String basePath;
    private final String lockName;
    private static final Integer MAX_RETRY_COUNT = 10;

    public BasicDistributedLock(ZooKeeper client, String basePath, String lockName) {
        this.client = client;
        this.basePath = basePath;
        this.path = basePath.concat("/").concat(lockName);
        this.lockName = lockName;
    }

    private void deleteOurPath(String ourPath) throws KeeperException, InterruptedException {
        client.delete(ourPath, -1);
    }

    private String createLockNode(ZooKeeper client, String path) throws KeeperException, InterruptedException {
        return client.create(path, null, OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    }

    private boolean waitToLock(long startMills, long millsToWait, String ourPath) {
        boolean haveTheLock = false;
        boolean doDelete = false;

//        try {
//            while (!haveTheLock) {
//                List<String> children = getSortedChildren();
//                String sequenceNodeName = ourPath.substring(basePath.length() + 1);
//                int ourIndex = children.indexOf(sequenceNodeName);
//
//                if (ourIndex < 0) {
//                    throw new RuntimeException("节点没有找到： " + sequenceNodeName);
//                }
//            }
//        }
        return true;
    }

}
