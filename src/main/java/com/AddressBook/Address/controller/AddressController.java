package com.AddressBook.Address.controller;

import com.AddressBook.Address.dto.AddressDTO;
import com.AddressBook.Address.interfaces.IAddressService;
import com.AddressBook.Address.model.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/addresses")
public class AddressController {
    @Autowired
    IAddressService iAddressService;

    @Autowired
    public AddressController(IAddressService iAddressService) {
        this.iAddressService = iAddressService;
    }

    @GetMapping
    public List<AddressDTO> getAll() {
        return iAddressService.getAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AddressDTO getById(@PathVariable Long id) {
        Address address = iAddressService.getById(id);
        return convertToDTO(address);
    }

    @PostMapping
    public AddressDTO add(@RequestBody AddressDTO addressDTO) {
        Address address = convertToEntity(addressDTO);
        Address savedAddress = iAddressService.save(address);
        return convertToDTO(savedAddress);
    }

    @PutMapping("/{id}")
    public AddressDTO update(@PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        Address address = convertToEntity(addressDTO);
        address.setId(id);
        Address updatedAddress = iAddressService.save(address);
        return convertToDTO(updatedAddress);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        iAddressService.delete(id);
    }

    private AddressDTO convertToDTO(Address address) {
        return new AddressDTO(address.getId(), address.getName(), address.getPhone(), address.getEmail(), address.getCity());
    }

    private Address convertToEntity(AddressDTO dto) {
        return new Address(dto.getId(), dto.getName(), dto.getPhone(), dto.getEmail(), dto.getCity());
    }
}
