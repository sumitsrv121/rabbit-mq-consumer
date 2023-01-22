package com.gehc.uls.subscriber.entity;

import com.rabbitmq.client.ConnectionFactory;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class ConnectionFactoryBuilder {
    public static final ConnectionFactory connectionFactory = new ConnectionFactory();


    public static ConnectionFactory getInstance() {
        return connectionFactory;
    }

    public static void setDetails(String username, String password, String virtualHost, String hostName, int portNumber) {
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setHost(hostName);
        connectionFactory.setPort(portNumber);
        if (portNumber == 5671) {
            try {
                connectionFactory.useSslProtocol();
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
