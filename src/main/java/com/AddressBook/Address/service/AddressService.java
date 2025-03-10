package com.AddressBook.Address.service;

import com.AddressBook.Address.dto.AddressDTO;
import com.AddressBook.Address.interfaces.IAddressService;
import com.AddressBook.Address.model.Address;
import com.AddressBook.Address.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService implements IAddressService {

    @Autowired
    private AddressRepository addressRepository;

    // When saving a new address or updating an existing one, update the cache.
    @Override
    @CachePut(value = "addresses", key = "#result.id")
    public AddressDTO save(AddressDTO addressDTO) {
        Address address = convertToEntity(addressDTO);
        Address savedAddress = addressRepository.save(address);
        return convertToDTO(savedAddress);
    }

    // Cache the result of the getAll method.
    @Override
    @Cacheable(value = "addresses")
    public List<AddressDTO> getAll() {
        return addressRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Cache individual address retrieval using the address ID as key.
    @Override
    @Cacheable(value = "addresses", key = "#id")
    public AddressDTO getById(Long id) {
        return addressRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    // Clear the cached address entry when it is deleted.
    @Override
    @CacheEvict(value = "addresses", key = "#id")
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
