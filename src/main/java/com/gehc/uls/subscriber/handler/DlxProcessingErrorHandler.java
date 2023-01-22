package com.gehc.uls.subscriber.handler;

import com.gehc.uls.subscriber.config.RabbitmqHeader;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;

public class DlxProcessingErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(DlxProcessingErrorHandler.class);
    /**
     * Dead exchange name
     */
    @NonNull
    private final String deadExchangeName;
    private int maxRetryCount = 3;

    /**
     * Constructor. Will retry for n times (default is 3) and on the next retry will
     * consider message as dead, put it on dead exchange with given
     * <code>dlxExchangeName</code> and <code>routingKey</code>
     *
     * @param deadExchangeName dead exchange name. Not a dlx for work queue, but
     *                         exchange name for really dead message (wont processed
     *                         antmore).
     * @throws IllegalArgumentException if <code>dlxExchangeName</code> or
     *                                  <code>dlxRoutingKey</code> is null or empty.
     */
    public DlxProcessingErrorHandler(String deadExchangeName) throws IllegalArgumentException {
        super();

        if (StringUtils.isAnyEmpty(deadExchangeName)) {
            throw new IllegalArgumentException("Must define dlx exchange name");
        }

        this.deadExchangeName = deadExchangeName;
    }

    /**
     * Constructor. Will retry for <code>maxRetryCount</code> times and on the next
     * retry will consider message as dead, put it on dead exchange with given
     * <code>dlxExchangeName</code> and <code>routingKey</code>
     *
     * @param deadExchangeName dead exchange name. Not a dlx for work queue, but
     *                         exchange name for really dead message (wont processed
     *                         antmore).
     * @param maxRetryCount    number of retry before message considered as dead (0
     *                         >= <code> maxRetryCount</code> >= 1000). If set less
     *                         than 0, will always retry
     * @throws IllegalArgumentException if <code>dlxExchangeName</code> or
     *                                  <code>dlxRoutingKey</code> is null or empty.
     */

    public DlxProcessingErrorHandler(String deadExchangeName, int maxRetryCount) {
        this(deadExchangeName);
        setMaxRetryCount(maxRetryCount);
    }

    public String getDeadExchangeName() {
        return deadExchangeName;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount) throws IllegalArgumentException {
        if (maxRetryCount > 1000) {
            throw new IllegalArgumentException("max retry must between 0-1000");
        }

        this.maxRetryCount = maxRetryCount;
    }

    /**
     * Handle AMQP message consume error. This default implementation will put
     * message to dead letter exchange for <code>maxRetryCount</code> times, thus
     * two variables are required when creating this object:
     * <code>dlxExchangeName</code> and <code>dlxRoutingKey</code>. <br/>
     * <code>maxRetryCount</code> is 3 by default, but you can set it using
     * <code>setMaxRetryCount(int)</code>
     *
     * @param message     AMQP message that caused error
     * @param channel     channel for AMQP message
     * @param deliveryTag message delivery tag
     */
    public void handleErrorProcessingMessage(Delivery message, Channel channel, long deliveryTag) {
        var rabbitmqHeader = new RabbitmqHeader(message.getProperties().getHeaders());
        try {
            if (rabbitmqHeader.getFailedRetryCount() >= maxRetryCount) {
                System.out.println("Failed retry count exceeded " + rabbitmqHeader.getFailedRetryCount());
                log.info("[DEAD] Error at " + LocalDateTime.now() + " on retry " + rabbitmqHeader.getFailedRetryCount()
                        + " for message " + new String(message.getBody()));
                channel.basicPublish(getDeadExchangeName(),
                        message.getEnvelope().getRoutingKey(),
                        null,
                        message.getBody());
                channel.basicAck(deliveryTag, false);
            } else {
                System.out.println("Failed retry count: " + rabbitmqHeader.getFailedRetryCount());
                log.info("[REQUEUE] Error at " + LocalDateTime.now() + " on retry "
                        + rabbitmqHeader.getFailedRetryCount() + " for message " + new String(message.getBody()));
                channel.basicReject(deliveryTag, false);
            }
        } catch (IOException e) {
            log.error("[HANDLER-FAILED] Error at " + LocalDateTime.now() + " on retry "
                    + rabbitmqHeader.getFailedRetryCount() + " for message " + new String(message.getBody()));
        }
    }
}
