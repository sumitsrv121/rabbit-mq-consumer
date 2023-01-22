package com.gehc.uls.subscriber.factory;

import com.gehc.uls.subscriber.config.ConnectionManager;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class ExchangeFactory {
    public static void declareExchange(String exchangeName, String exchangeType) throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();
        // Declare exchange based on the type define
        Optional.ofNullable(exchangeType)
                .ifPresent(eType -> {
                    eType = eType.trim().toLowerCase();
                    switch (eType) {
                        case "fanout" -> {
                            try {
                                channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT, true);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        case "direct" -> {
                            try {
                                channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT, true);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        case "topic" -> {
                            try {
                                channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC, true);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        case "headers" -> {
                            try {
                                channel.exchangeDeclare(exchangeName, BuiltinExchangeType.HEADERS, true);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        default -> throw new RuntimeException("No supported exchange type found...");
                    }
                });
        channel.close();
    }
}
