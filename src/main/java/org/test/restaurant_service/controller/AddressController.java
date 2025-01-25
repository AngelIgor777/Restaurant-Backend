package org.test.restaurant_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.entity.Address;
import org.test.restaurant_service.service.AddressService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * Get an address by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddressById(@PathVariable Integer id) {
        Address address = addressService.findById(id);
        return ResponseEntity.ok(address);
    }

    /**
     * Get all addresses
     */
    @GetMapping
    public ResponseEntity<List<Address>> getAllAddresses() {
        List<Address> addresses = addressService.findAll();
        return ResponseEntity.ok(addresses);
    }

    /**
     * Get addresses by city
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<List<Address>> getAddressesByCity(@PathVariable String city) {
        List<Address> addresses = addressService.findByCity(city);
        return ResponseEntity.ok(addresses);
    }

    /**
     * Get addresses by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Address> getAddressesByUserId(@PathVariable Integer userId) {
        Address address = addressService.findByUserId(userId);
        return ResponseEntity.ok(address);
    }

    /**
     * Create or update an address
     */
    @PostMapping
    public ResponseEntity<Address> saveAddress(@RequestBody Address address) {
        Address savedAddress = addressService.save(address);
        return ResponseEntity.ok(savedAddress);
    }

    /**
     * Delete an address by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Integer id) {
        addressService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
