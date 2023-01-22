package com.gehc.uls.subscriber.factory;

import com.gehc.uls.subscriber.config.ConnectionManager;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class QueueFactory {
    public static void declareQueue(String queueName) throws IOException, TimeoutException {
        //Create a channel - do not share the Channel instance
        Channel channel = ConnectionManager.getConnection().createChannel();
        //queueDeclare  - (queueName, durable, exclusive, autoDelete, arguments)
        channel.queueDeclare(queueName, true, false, false, null);
        channel.close();
    }

    public static void declareQueueWithArguments(String queueName, Map<String, Object> arguments) throws IOException, TimeoutException {
        //Create a channel - do not share the Channel instance
        Channel channel = ConnectionManager.getConnection().createChannel();
        //queueDeclare  - (queueName, durable, exclusive, autoDelete, arguments)
        channel.queueDeclare(queueName, true, false, false, arguments);
        channel.close();
    }
}
