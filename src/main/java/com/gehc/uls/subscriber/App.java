package com.gehc.uls.subscriber;

import com.gehc.uls.subscriber.config.ConnectionManager;

public class App {
    public static void main(String[] args) throws Exception {
        var connection = ConnectionManager.getConnection();
        System.out.println(connection);
        //QueueFactory.declareQueue("q.guideline.image.work");
        // System.out.println(connection + "----" + connection.getId());
        //MessageSubscriber messageSubscriber = new MessageSubscriber();
        //messageSubscriber.subscribeMessage("q.guideline.image.work", (message) -> {
        //throw new RuntimeException();
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            System.out.println("Woke out from sleep");
        //});
        //Connection connection1 = ConnectionManager.getConnection();
    }
}
