package com.chandler.zookeeper.chapter3;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class Master implements Watcher {

    ZooKeeper zk;
    String hostPort;

    Master(String hostPort) {
        this.hostPort = hostPort;
    }

    void startZK() {
        try {
            zk = new ZooKeeper(hostPort, 15000, this);
            long sessionId = zk.getSessionId();
            System.out.println("sessionId: " + sessionId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void stopZK() throws InterruptedException {
        zk.close();
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println(watchedEvent);
    }

    public static void main(String[] args) throws InterruptedException {
        Master m = new Master("127.0.0.1:2181");
        m.startZK();
        Thread.sleep(30000);
//        m.stopZK();
    }

}
