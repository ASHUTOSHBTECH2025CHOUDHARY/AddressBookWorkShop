package com.AddressBook.Address.config;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue userQueue() {
        return new Queue("userQueue", true);
    }

    @Bean
    public Queue contactQueue() {
        return new Queue("contactQueue", true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("appExchange");
    }

    @Bean
    public Binding bindUserQueue(Queue userQueue, DirectExchange exchange) {
        return BindingBuilder.bind(userQueue).to(exchange).with("userRoutingKey");
    }

    @Bean
    public Binding bindContactQueue(Queue contactQueue, DirectExchange exchange) {
        return BindingBuilder.bind(contactQueue).to(exchange).with("contactRoutingKey");
    }
}
