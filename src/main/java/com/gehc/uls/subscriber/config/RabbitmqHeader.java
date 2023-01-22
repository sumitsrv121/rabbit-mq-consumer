package com.gehc.uls.subscriber.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Data
public class RabbitmqHeader {
    private static final String KEYWORD_QUEUE_WAIT = "wait";
    private final List<RabbitmqHeaderXDeath> xDeaths = new ArrayList<>(2);
    private String xFirstDeathReason = StringUtils.EMPTY;
    private String xFirstDeathQueue = StringUtils.EMPTY;
    private String xFirstDeathExchange = StringUtils.EMPTY;

    @SuppressWarnings("unchecked")
    public RabbitmqHeader(Map<String, Object> headers) {
        if (headers != null) {
            var xFirstDeathExchange =
                    Optional.ofNullable(headers.get("x-first-death-exchange"));
            var xFirstDeathQueue =
                    Optional.ofNullable(headers.get("x-first-death-queue"));
            var xFirstDeathReason =
                    Optional.ofNullable(headers.get("x-first-death-reason"));

            xFirstDeathExchange.ifPresent(s -> this.setXFirstDeathExchange(s.toString()));
            xFirstDeathQueue.ifPresent(s -> this.setXFirstDeathQueue(s.toString()));
            xFirstDeathReason.ifPresent(s -> this.setXFirstDeathReason(s.toString()));

            var xDeathHeader = (List<Map<String, Object>>) headers.get("x-death");
            if (xDeathHeader != null) {
                for (Map<String, Object> x : xDeathHeader) {
                    RabbitmqHeaderXDeath headerXDeath = new RabbitmqHeaderXDeath();
                    var reason = Optional.ofNullable(x.get("reason"));
                    var count = Optional.ofNullable(x.get("count"));
                    var exchange = Optional.ofNullable(x.get("exchange"));
                    var queue = Optional.ofNullable(x.get("queue"));
                    var routingKeys = Optional.ofNullable(x.get("routing-keys"));
                    var time = Optional.ofNullable(x.get("time"));

                    reason.ifPresent(r -> headerXDeath.setReason(r.toString()));
                    count.ifPresent(c -> headerXDeath.setCount(Integer.parseInt(c.toString())));
                    exchange.ifPresent(e -> headerXDeath.setExchange(e.toString()));
                    queue.ifPresent(q -> headerXDeath.setQueue(q.toString()));
                    routingKeys.ifPresent(r -> headerXDeath.setRoutingKeys((List<String>) r));
                    time.ifPresent(t -> headerXDeath.setTime((Date) t));

                    xDeaths.add(headerXDeath);
                }
            }
        }
    }

    public int getFailedRetryCount() {
        for (var xDeath : xDeaths) {
            if (xDeath.getQueue().toLowerCase().contains(KEYWORD_QUEUE_WAIT) &&
                    xDeath.getExchange().toLowerCase().contains(KEYWORD_QUEUE_WAIT)) {
                return xDeath.getCount();
            }
        }
        return 0;
    }
}