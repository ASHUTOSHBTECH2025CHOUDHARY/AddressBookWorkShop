package com.AddressBook.Address.controller;

import com.AddressBook.Address.dto.AddressDTO;
import com.AddressBook.Address.interfaces.IAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
public class AddressController {

    private final IAddressService iAddressService;

    @Autowired
    public AddressController(IAddressService iAddressService) {
        this.iAddressService = iAddressService;
    }

    @GetMapping
    public List<AddressDTO> getAll() {
        return iAddressService.getAll();
    }

    @GetMapping("/{id}")
    public AddressDTO getById(@PathVariable Long id) {
        return iAddressService.getById(id);
    }

    @PostMapping
    public AddressDTO add(@RequestBody AddressDTO addressDTO) {
        return iAddressService.save(addressDTO);
    }

    @PutMapping("/{id}")
    public AddressDTO update(@PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        addressDTO.setId(id); // Ensure the ID is set correctly
        return iAddressService.save(addressDTO);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        iAddressService.delete(id);
    }
}
