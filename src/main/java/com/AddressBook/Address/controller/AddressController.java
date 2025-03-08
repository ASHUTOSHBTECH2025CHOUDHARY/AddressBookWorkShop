package com.AddressBook.Address.controller;

import com.AddressBook.Address.model.Address;
import com.AddressBook.Address.service.AddressService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
public class AddressController {
    private final AddressService service;

    public AddressController(AddressService service) {
        this.service = service;
    }

    @GetMapping
    public List<Address> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public Address getById(@PathVariable Long id) { return service.getById(id); }

    @PostMapping
    public Address add(@RequestBody Address address) { return service.save(address); }

    @PutMapping("/{id}")
    public Address update(@PathVariable Long id, @RequestBody Address address) {
        address.setId(id);
        return service.save(address);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.delete(id); }
}