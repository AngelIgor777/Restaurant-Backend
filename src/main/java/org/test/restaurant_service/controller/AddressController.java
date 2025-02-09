package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.request.AddressRequestDTO;
import org.test.restaurant_service.dto.response.AddressResponseDTO;
import org.test.restaurant_service.entity.Address;
import org.test.restaurant_service.mapper.AddressMapper;
import org.test.restaurant_service.service.AddressService;
import org.test.restaurant_service.service.UserAddressService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final AddressService addressService;
    private final AddressMapper addressMapper;
    private final UserAddressService userAddressService;

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


    @PostMapping
    public ResponseEntity<AddressResponseDTO> saveAddress(@RequestBody AddressRequestDTO addressRequestDTO) {
        Address address = addressMapper.toEntity(addressRequestDTO);
        Address savedAddress = userAddressService.saveAddressToUser(address, addressRequestDTO.getUserUUID());

        AddressResponseDTO responseDto = addressMapper.toResponseDto(savedAddress);

        //require because uuid not mapped automatically
        responseDto.setUserUUID(savedAddress.getUser().getUuid());
        return ResponseEntity.ok(responseDto);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Integer id) {
        addressService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
