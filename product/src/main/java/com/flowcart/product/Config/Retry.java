package com.flowcart.product.Config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class Retry {
    @Bean
public NewTopic orderTopic() {
    return TopicBuilder.name("order-events").partitions(3).replicas(1).build();
}

@Bean
public NewTopic retry1() {
    return TopicBuilder.name("order-events-retry-1")
            .partitions(3)
            .replicas(1)
            .build();
}

@Bean
public NewTopic retry2() {
    return TopicBuilder.name("order-events-retry-2")
            .partitions(3)
            .replicas(1)
            .build();
}

@Bean
public NewTopic dlt() {
    return TopicBuilder.name("order-events-dlt")
            .partitions(3)
            .replicas(1)
            .build();
}
}
