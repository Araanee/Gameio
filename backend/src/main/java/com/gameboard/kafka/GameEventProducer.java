package com.gameboard.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class GameEventProducer {

    private static final String TOPIC = "game-imported";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendGameImported(Long gameId, String title, String source) {
        String payload = "{\"gameId\":" + gameId +
                ",\"title\":\"" + title +
                "\",\"source\":\"" + source +
                "\",\"importedAt\":\"" + Instant.now() + "\"}";
        kafkaTemplate.send(TOPIC, String.valueOf(gameId), payload);
    }
}
