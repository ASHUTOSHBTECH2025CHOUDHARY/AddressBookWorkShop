package com.AddressBook.Address.interfaces;

import com.AddressBook.Address.model.Address;

import java.util.List;

public interface IAddressService {
    Address save(Address address);
    List<Address> getAll();
    Address getById(Long id);
    void delete(Long id);
}
