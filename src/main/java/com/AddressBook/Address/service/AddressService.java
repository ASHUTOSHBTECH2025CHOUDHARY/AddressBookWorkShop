package com.AddressBook.Address.service;

import com.AddressBook.Address.interfaces.IAddressService;
import com.AddressBook.Address.model.Address;
import com.AddressBook.Address.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService implements IAddressService {
    private final AddressRepository repository;

    public AddressService(AddressRepository repository) {
        this.repository = repository;
    }

    public List<Address> getAll() { return repository.findAll(); }

    public Address getById(Long id) { return repository.findById(id).orElse(null); }

    public Address save(Address address) { return repository.save(address); }

    public void delete(Long id) { repository.deleteById(id); }
}