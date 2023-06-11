package com.ezekielwong.ms.docs.utils;

import com.ezekielwong.ms.docs.service.ClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Utility class for managing Kafka messaging
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaUtils {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ClientService clientService;

    public String createMessage(Object content, String topic, String correlationId) throws JsonProcessingException {

        Map<String, Object> map = new HashMap<>();
        map.put("topic", topic);
        map.put("correlationId", correlationId);
        map.put("content", content);

        ObjectMapper mapper = new ObjectMapper();
        String message = mapper.writeValueAsString(map);
        log.debug(message);

        return message;
    }

    public void sendMessage(String topic, String correlationId, String message, String caseId) {

        log.debug("Sending Kafka message");

//        try {
//            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, correlationId, message);
//
//            future.whenComplete()
//        }
    }
}
