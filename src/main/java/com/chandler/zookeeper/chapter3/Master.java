package com.chandler.zookeeper.chapter3;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

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

    String serverId = Integer.toHexString((int)(Math.random()*100));
    boolean isLeader = false;

    boolean checkMaster() {
        while(true) {
            try {
                Stat stat = new Stat();
                byte data[] = zk.getData("/master", false, stat);
                isLeader = new String(data).equals(serverId);
                return isLeader;
            } catch (KeeperException.NoNodeException e) {
                return false;
            } catch(KeeperException.ConnectionLossException e) {

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (KeeperException e) {
                e.printStackTrace();
            }
        }
    }

    void runForMaster() throws InterruptedException {
        while(true) {
            try {
                zk.create("/master",
                        serverId.getBytes(),
                        OPEN_ACL_UNSAFE,
                        CreateMode.EPHEMERAL
                );
                isLeader = true;
                break;
            } catch (KeeperException.NodeExistsException e) {
                isLeader = false;
                break;
            } catch (KeeperException.ConnectionLossException e) {

            } catch (KeeperException e) {

            }
            if(checkMaster()) break;
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println(watchedEvent);
    }

    public static void main(String[] args) throws InterruptedException {

        Master m = new Master("127.0.0.1:2181");
        m.startZK();
        m.runForMaster();
        if(m.isLeader) {
            System.out.println("I am the Leader");
            Thread.sleep(30000);
        } else{
            System.out.println("Some one else is the leader");
        }
        m.stopZK();
    }



}
