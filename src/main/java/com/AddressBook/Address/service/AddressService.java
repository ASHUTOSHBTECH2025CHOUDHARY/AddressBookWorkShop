package com.AddressBook.Address.service;

import com.AddressBook.Address.dto.AddressDTO;
import com.AddressBook.Address.interfaces.IAddressService;
import com.AddressBook.Address.model.Address;
import com.AddressBook.Address.repository.AddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    private static final Logger logger = LoggerFactory.getLogger(AddressService.class); // SLF4J Logger

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String CACHE_KEY = "addresses";

    @Override
    @Cacheable(value = "addresses")
    public List<AddressDTO> getAll() {
        logger.info("Fetching all addresses from the database");
        List<AddressDTO> cachedAddresses = (List<AddressDTO>) redisTemplate.opsForValue().get(CACHE_KEY);
        if (cachedAddresses != null) {
            logger.debug("Returning cached addresses: {}", cachedAddresses.size());
            return cachedAddresses;
        }
        List<AddressDTO> addresses = addressRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        redisTemplate.opsForValue().set(CACHE_KEY, addresses, 10, TimeUnit.MINUTES);
        logger.debug("Cached {} addresses for 10 minutes", addresses.size());
        return addresses;
    }

    @Override
    @Cacheable(value = "addresses", key = "#id")
    public AddressDTO getById(Long id) {
        logger.info("Fetching address with ID: {}", id);
        return addressRepository.findById(id)
                .map(this::convertToDTO)
                .orElseGet(() -> {
                    logger.warn("No address found with ID: {}", id);
                    return null;
                });
    }

    @Override
    @CacheEvict(value = "addresses", allEntries = true)
    public AddressDTO save(AddressDTO addressDTO) {
        logger.info("Saving address: {}", addressDTO);
        Address address = convertToEntity(addressDTO);
        Address savedAddress = addressRepository.save(address);
        redisTemplate.delete(CACHE_KEY);

        // Publish message to RabbitMQ
        rabbitTemplate.convertAndSend("AddressBookExchange", "addressKey", addressDTO.getEmail());
        logger.debug("Published address email {} to RabbitMQ", addressDTO.getEmail());

        return convertToDTO(savedAddress);
    }

    @Override
    @CacheEvict(value = "addresses", allEntries = true)
    public void delete(Long id) {
        logger.info("Deleting address with ID: {}", id);
        addressRepository.deleteById(id);
        redisTemplate.delete(CACHE_KEY);
        logger.debug("Deleted address with ID: {} and cleared cache", id);
    }

    private AddressDTO convertToDTO(Address address) {
        return new AddressDTO(address.getId(), address.getName(), address.getPhone(), address.getEmail(), address.getCity());
    }

    private Address convertToEntity(AddressDTO dto) {
        return new Address(dto.getId(), dto.getName(), dto.getPhone(), dto.getEmail(), dto.getCity());
    }
}