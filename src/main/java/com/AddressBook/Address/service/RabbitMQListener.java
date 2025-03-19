package com.AddressBook.Address.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQListener {

    @RabbitListener(queues = "UserQueue")
    public void handleUserRegistration(String email) {
        System.out.println("📧 Sending registration email to: " + email);
    }

    @RabbitListener(queues = "AddressQueue")
    public void handleNewAddress(String email) {
        System.out.println("📍 New address added: " + email);
    }
}