package com.flowcart.product.Config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {
    @Bean
public DefaultErrorHandler errorHandler(KafkaTemplate<String, String> kafkaTemplate) {

    DeadLetterPublishingRecoverer recoverer =
        new DeadLetterPublishingRecoverer(kafkaTemplate,
            (record, ex) -> {

                if (record.topic().equals("order-events")) {
                    return new TopicPartition("order-events-retry-1", record.partition());
                }
                else if (record.topic().equals("order-events-retry-1")) {
                    return new TopicPartition("order-events-retry-2", record.partition());
                }
                else {
                    return new TopicPartition("order-events-dlt", record.partition());
                }
            });

    return new DefaultErrorHandler(
        recoverer,
        new FixedBackOff(2000L, 2) // retry twice before moving
    );
}
// Fail in order-events
// → retry 2 times
// → move to retry-1

// Fail again
// → retry 2 times
// → move to retry-2

// Fail again
// → move to DLT
}
