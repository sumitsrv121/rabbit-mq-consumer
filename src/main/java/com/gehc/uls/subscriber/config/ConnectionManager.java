package com.gehc.uls.subscriber.config;

import com.gehc.uls.subscriber.entity.ConnectionFactoryBuilder;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConnectionManager {
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                ConnectionFactory connectionFactory = ConnectionFactoryBuilder.getInstance();
                connection = connectionFactory.newConnection();
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }
}
