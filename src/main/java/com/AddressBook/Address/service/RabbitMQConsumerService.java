package com.AddressBook.Address.service;

import com.AddressBook.Address.dto.AddressDTO;
import com.AddressBook.Address.dto.UserDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumerService {

    @Async
    @RabbitListener(queues = "${rabbitmq.user.queue.name}")
    public void consumeUserRegistration(UserDTO userDTO) {
        System.out.println("📩 Processing User Registration: " + userDTO);

        // Simulate email sending (Replace with real email service)
        System.out.println("📧 Sending Welcome Email to: " + userDTO.getEmail());
    }

    @Async
    @RabbitListener(queues = "${rabbitmq.contact.queue.name}")
    public void consumeContactAddition(AddressDTO addressDTO) {
        System.out.println("📩 Processing Contact Addition: " + addressDTO);
    }
}
