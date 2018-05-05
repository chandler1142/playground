package com.chandler.thread;

public class RunnerTest {

    public static void main(String[] args) {
        MyThread myThread = new MyThread();
        myThread.start();
//        myThread.interrupt();
        Thread.currentThread().interrupt();
        System.out.println("第一次调用myThread.interrupted(),返回值："+myThread.interrupted());
        System.out.println("第二次调用myThread.interrupted(),返回值："+myThread.interrupted());
        System.out.println("============end===================");
    }


    public static class MyThread extends Thread{

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                super.run();
                System.out.println("i="+(i+1));
            }
        }

    }
}

