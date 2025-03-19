package com.AddressBook.Address.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue userQueue() {
        return new Queue("UserQueue", true);
    }

    @Bean
    public Queue addressQueue() {
        return new Queue("AddressQueue", true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("AddressBookExchange");
    }

    @Bean
    public Binding userBinding(Queue userQueue, TopicExchange exchange) {
        return BindingBuilder.bind(userQueue).to(exchange).with("userKey");
    }

    @Bean
    public Binding addressBinding(Queue addressQueue, TopicExchange exchange) {
        return BindingBuilder.bind(addressQueue).to(exchange).with("addressKey");
    }
}
