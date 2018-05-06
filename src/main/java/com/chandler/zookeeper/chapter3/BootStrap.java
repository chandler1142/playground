package com.chandler.zookeeper.chapter3;


import org.apache.zookeeper.*;
import org.slf4j.*;

import java.io.IOException;

public class BootStrap implements Watcher {

    private static final Logger LOG = LoggerFactory.getLogger(BootStrap.class);

    ZooKeeper zk;
    String hostPort;

    BootStrap(String hostPort) {
        this.hostPort = hostPort;
    }

    void start() throws IOException {
        zk = new ZooKeeper(hostPort, 15000, this);
    }

    public void bootstrap() {
        createParent("/workers", "worker-name".getBytes());
        createParent("/status", "status-name".getBytes());
        createParent("/tasks", new byte[0]);
        createParent("/assign", new byte[0]);
    }

    void createParent(String path, byte[] data) {
        zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, createParentCallback, data);
    }

    AsyncCallback.StringCallback createParentCallback = new AsyncCallback.StringCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, String name) {
            System.out.print("path: " + name + " ");
            System.out.println("name: " + name);
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    createParent(path, (byte[]) ctx);
                    break;
                case OK:
                    LOG.info("Parent created");
                    break;
                case NODEEXISTS:
                    LOG.warn("Parent already registered: " + path);
                    break;
                default:
                    LOG.error("Something went wrong: ", KeeperException.create(KeeperException.Code.get(rc), path));
            }
        }
    };

    @Override
    public void process(WatchedEvent event) {
        System.out.println(event);
    }

    void close() throws InterruptedException {
        zk.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        BootStrap bootStrap = new BootStrap("127.0.0.1:2181");
        bootStrap.start();
        bootStrap.bootstrap();
        Thread.sleep(60000);
        bootStrap.close();
    }
}
