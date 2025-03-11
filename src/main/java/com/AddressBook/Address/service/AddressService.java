package com.AddressBook.Address.service;

import com.AddressBook.Address.dto.AddressDTO;
import com.AddressBook.Address.interfaces.IAddressService;
import com.AddressBook.Address.model.Address;
import com.AddressBook.Address.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService implements IAddressService {
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.contact.routing.key}")
    private String contactRoutingKey;
    @Override
    public AddressDTO save(AddressDTO addressDTO) {
        Address address = convertToEntity(addressDTO);
        Address savedAddress = addressRepository.save(address);

        // Publish event to RabbitMQ
        rabbitTemplate.convertAndSend(exchange, contactRoutingKey, addressDTO);
        System.out.println("📨 Sent Contact Addition Event to RabbitMQ: " + addressDTO);

        return convertToDTO(savedAddress);
    }

    @Override
    public List<AddressDTO> getAll() {
        return addressRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AddressDTO getById(Long id) {
        return addressRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    public void delete(Long id) {
        addressRepository.deleteById(id);
    }

    private AddressDTO convertToDTO(Address address) {
        return new AddressDTO(address.getId(), address.getName(), address.getPhone(), address.getEmail(), address.getCity());
    }

    private Address convertToEntity(AddressDTO dto) {
        return new Address(dto.getId(), dto.getName(), dto.getPhone(), dto.getEmail(), dto.getCity());
    }
}
