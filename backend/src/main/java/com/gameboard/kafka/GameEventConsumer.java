package com.gameboard.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class GameEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(GameEventConsumer.class);

    @KafkaListener(topics = "game-imported", groupId = "gameboard-consumer")
    public void onGameImported(String payload) {
        log.info("[Kafka] game-imported received: {}", payload);
    }
}
