package com.chandler.zookeeper.chapter3;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class Worker implements Watcher {

    private static final Logger LOG = LoggerFactory.getLogger(Worker.class);

    ZooKeeper zk;
    String hostPort;
    String severId = Integer.toHexString((int)(Math.random()*100));

    Worker(String hostPort) {
        this.hostPort = hostPort;
    }

    void startZK() throws IOException {
        zk = new ZooKeeper(hostPort, 15000, this);
    }

    @Override
    public void process(WatchedEvent event) {
        LOG.info(event.toString() + ", " + hostPort);
    }

    void register() {
        zk.create("/workers/worker-" + severId,
                "Idle".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL,
                createWorkerCallback,
                null
        );
    }

    AsyncCallback.StringCallback createWorkerCallback = new AsyncCallback.StringCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    register();
                    break;
                case OK:
                    LOG.info("Registered successfully: " + severId);
                    break;
                case NODEEXISTS:
                    LOG.warn("Already registered: " + severId);
                    break;
                default:
                    LOG.error("Something went wrong: " + KeeperException.create(KeeperException.Code.get(rc)),path);
            }
        }
    };

    public static void main(String[] args) throws IOException, InterruptedException {
        Worker w = new Worker("127.0.0.1:2181");
        w.startZK();
        w.register();
        Thread.sleep(60000);
    }

}
