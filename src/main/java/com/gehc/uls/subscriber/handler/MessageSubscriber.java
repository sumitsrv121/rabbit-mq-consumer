package com.gehc.uls.subscriber.handler;

import com.gehc.uls.subscriber.config.ConnectionManager;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.function.Consumer;

@Slf4j
public class MessageSubscriber {
    private static final String DEAD_EXCHANGE_NAME = "x.guideline.dead";
    private final DlxProcessingErrorHandler dlxProcessingErrorHandler;

    public MessageSubscriber() {
        this.dlxProcessingErrorHandler = new DlxProcessingErrorHandler(DEAD_EXCHANGE_NAME);
    }

    public void subscribeMessage(final String queueName, final Consumer<String> messageConsumer) throws IOException {
        Channel channel = ConnectionManager.getConnection().createChannel();
        // channel.basicConsume(CommonConfigs.DEFAULT_QUEUE, autoAck, deliveryCallback, cancelCallback)
        channel.basicConsume(queueName, false, ((consumerTag, message) -> {

            String messageFromQueueAsString = new String(message.getBody());
            long deliveryTag = message.getEnvelope().getDeliveryTag();

            try {
                messageConsumer.accept(messageFromQueueAsString);
                channel.basicAck(deliveryTag, false);
            } catch (Exception e) {
                // channel.basicPublish("", "dlq.course", null, messageFromQueueAsString.getBytes(StandardCharsets.UTF_8));
                log.error("Error processing message : {} : {}", new String(message.getBody()), e.getMessage());
                dlxProcessingErrorHandler.handleErrorProcessingMessage(message, channel, deliveryTag);
            }

        }), consumerTag -> {
            log.warn("Operation got cancelled, ConsumerTag {}", consumerTag);
        });
    }
}
