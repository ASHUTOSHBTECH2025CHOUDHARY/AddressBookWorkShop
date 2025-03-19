package com.AddressBook.Address.controller;

import com.AddressBook.Address.dto.AddressDTO;
import com.AddressBook.Address.interfaces.IAddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
public class AddressController {

    private static final Logger logger = LoggerFactory.getLogger(AddressController.class); // SLF4J Logger

    @Autowired
    IAddressService iAddressService;

    @GetMapping
    public List<AddressDTO> getAll() {
        logger.info("Received request to fetch all addresses");
        List<AddressDTO> addresses = iAddressService.getAll();
        logger.debug("Returning {} addresses", addresses.size());
        return addresses;
    }

    @GetMapping("/{id}")
    public AddressDTO getById(@PathVariable Long id) {
        logger.info("Received request to fetch address with ID: {}", id);
        AddressDTO address = iAddressService.getById(id);
        if (address == null) {
            logger.warn("Address with ID: {} not found", id);
        }
        return address;
    }

    @PostMapping
    public AddressDTO add(@RequestBody AddressDTO addressDTO) {
        logger.info("Received request to add new address: {}", addressDTO);
        AddressDTO savedAddress = iAddressService.save(addressDTO);
        logger.debug("Address saved successfully: {}", savedAddress);
        return savedAddress;
    }

    @PutMapping("/{id}")
    public AddressDTO update(@PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        logger.info("Received request to update address with ID: {}", id);
        addressDTO.setId(id); // Ensure the ID is set correctly
        AddressDTO updatedAddress = iAddressService.save(addressDTO);
        logger.debug("Address updated successfully: {}", updatedAddress);
        return updatedAddress;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        logger.info("Received request to delete address with ID: {}", id);
        iAddressService.delete(id);
        logger.debug("Address with ID: {} deleted successfully", id);
    }
}