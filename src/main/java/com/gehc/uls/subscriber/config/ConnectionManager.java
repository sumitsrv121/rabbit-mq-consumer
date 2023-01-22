package com.gehc.uls.subscriber.config;

import com.gehc.uls.subscriber.builder.ConnectionFactoryBuilder;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConnectionManager {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionManager.class);
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                LOG.debug("Building connection factory....");
                ConnectionFactory connectionFactory = ConnectionFactoryBuilder.getInstance();
                LOG.debug("Trying to create a new connection.....");
                connection = connectionFactory.newConnection();
                LOG.debug("Successfully created a new connection.....");
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }
}
