package com.gehc.uls.subscriber.factory;

import com.gehc.uls.subscriber.config.ConnectionManager;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class BindingFactory {
    private static String routingKey;

    public static void declareBinding(String queueName, String exchangeName, String routingKey)
            throws IOException, TimeoutException {
        Optional.ofNullable(routingKey).ifPresentOrElse(rKey -> BindingFactory.routingKey = rKey,
                () -> BindingFactory.routingKey = "");

        Channel channel = ConnectionManager.getConnection().createChannel();
        //Create bindings - (queue, exchange, routingKey)
        channel.queueBind(queueName, exchangeName, BindingFactory.routingKey);
        channel.close();
    }

    public static void declareBindingWithArguments(String queueName, String exchangeName,
                                                   String routingKey,
                                                   Map<String, Object> arguments)
            throws IOException, TimeoutException {
        Optional.ofNullable(routingKey).ifPresentOrElse(rKey -> BindingFactory.routingKey = rKey,
                () -> BindingFactory.routingKey = "");

        Channel channel = ConnectionManager.getConnection().createChannel();
        //Create bindings - (queue, exchange, routingKey, headerArguments)
        channel.queueBind(queueName, exchangeName, BindingFactory.routingKey, arguments);
        channel.close();
    }
}
