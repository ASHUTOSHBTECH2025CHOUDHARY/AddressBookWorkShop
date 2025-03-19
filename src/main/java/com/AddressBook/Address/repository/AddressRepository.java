package com.AddressBook.Address.repository;

import com.AddressBook.Address.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}