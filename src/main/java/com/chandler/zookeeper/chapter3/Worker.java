package com.chandler.zookeeper.chapter3;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Worker implements Watcher {

    private static final Logger LOG = LoggerFactory.getLogger(Worker.class);

    ZooKeeper zk;
    String hostPort;
    String severId = Integer.toHexString((int) (Math.random() * 100));

    String status;
    String name;

    public void setStatus(String status) {
        this.status = status;
        updateStatus(status);
    }

    synchronized private void updateStatus(String status) {
        if (status == this.status) {
            zk.setData("/workers/" + name, status.getBytes(), -1, statusUpdateCallback, status);
        }
    }

    AsyncCallback.StatCallback statusUpdateCallback = new AsyncCallback.StatCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, Stat stat) {
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    updateStatus((String) ctx);
                    return;
            }
        }
    };


    Worker(String hostPort) {
        this.hostPort = hostPort;
    }

    void startZK() throws IOException {
        zk = new ZooKeeper(hostPort, 15000, this);
    }

    void stopZK() throws InterruptedException {
        zk.close();
    }

    @Override
    public void process(WatchedEvent event) {
        LOG.info(event.toString() + ", " + hostPort);
    }

    void register() {
        this.name = "worker-" + severId;
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
                    LOG.error("Something went wrong: " + KeeperException.create(KeeperException.Code.get(rc)), path);
            }
        }
    };

    public static void main(String[] args) throws IOException, InterruptedException {
        Worker w = new Worker("127.0.0.1:2181");
        w.startZK();
        w.register();
        Thread.sleep(60000);
        w.stopZK();
    }

}
