package com.AddressBook.Address.service;

import com.AddressBook.Address.dto.AddressDTO;
import com.AddressBook.Address.interfaces.IAddressService;
import com.AddressBook.Address.model.Address;
import com.AddressBook.Address.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class AddressService implements IAddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY = "addresses";

    @Override
    @Cacheable(value = "addresses")
    public List<AddressDTO> getAll() {
        List<AddressDTO> cachedAddresses = (List<AddressDTO>) redisTemplate.opsForValue().get(CACHE_KEY);
        if (cachedAddresses != null) {
            return cachedAddresses;
        }
        List<AddressDTO> addresses = addressRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        redisTemplate.opsForValue().set(CACHE_KEY, addresses, 10, TimeUnit.MINUTES);

        return addresses;
    }

    @Override
    @Cacheable(value = "addresses", key = "#id")
    public AddressDTO getById(Long id) {
        return addressRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    @CacheEvict(value = "addresses", allEntries = true)
    public AddressDTO save(AddressDTO addressDTO) {
        Address address = convertToEntity(addressDTO);
        Address savedAddress = addressRepository.save(address);
        redisTemplate.delete(CACHE_KEY);

        return convertToDTO(savedAddress);
    }

    @Override
    @CacheEvict(value = "addresses", allEntries = true)
    public void delete(Long id) {
        addressRepository.deleteById(id);
        redisTemplate.delete(CACHE_KEY);
    }

    private AddressDTO convertToDTO(Address address) {
        return new AddressDTO(address.getId(), address.getName(), address.getPhone(), address.getEmail(), address.getCity());
    }

    private Address convertToEntity(AddressDTO dto) {
        return new Address(dto.getId(), dto.getName(), dto.getPhone(), dto.getEmail(), dto.getCity());
    }
}