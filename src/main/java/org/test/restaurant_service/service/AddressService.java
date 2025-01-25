package org.test.restaurant_service.service;

import org.test.restaurant_service.entity.Address;

import java.util.List;

public interface AddressService {

    // Find an address by its ID
    Address findById(Integer id);

    // Save or update an address
    Address save(Address address);

    // Delete an address by its ID
    void deleteById(Integer id);

    // Find all addresses
    List<Address> findAll();

    // Find addresses by city
    List<Address> findByCity(String city);

    // Find addresses by user ID
    Address findByUserId(Integer userId);
}
