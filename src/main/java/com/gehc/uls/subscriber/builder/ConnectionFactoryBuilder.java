package com.gehc.uls.subscriber.builder;

import com.rabbitmq.client.ConnectionFactory;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class ConnectionFactoryBuilder {
    private static final ConnectionFactory connectionFactory = new ConnectionFactory();


    public static ConnectionFactory getInstance() {
        return connectionFactory;
    }

    public static void setDetails(String username, String password, String virtualHost, String hostName, int portNumber, String useSsl) {
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setHost(hostName);
        connectionFactory.setPort(portNumber);

        if (virtualHost != null && !virtualHost.isEmpty()) {
            connectionFactory.setVirtualHost(virtualHost);
        } else {
            connectionFactory.setVirtualHost("/");
        }

        if (useSsl != null && !useSsl.isEmpty() && useSsl.equalsIgnoreCase("true")) {
            try {
                connectionFactory.useSslProtocol();
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
