package com.AddressBook.Address.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQListener {

    @RabbitListener(queues = "UserQueue")
    public void handleUserEvent(String email) {
        System.out.println("📧 Sending registration email to: " + email);
    }

    @RabbitListener(queues = "AddressQueue")
    public void handleAddressEvent(String message) {
        System.out.println("📍 New address added: " + message);
    }
}