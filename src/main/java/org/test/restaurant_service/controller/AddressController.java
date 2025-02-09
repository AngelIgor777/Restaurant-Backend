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
import java.util.UUID;

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

    @PatchMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@PathVariable Integer id, @RequestBody AddressRequestDTO addressRequestDTO) {
        Address existingAddress = addressService.findById(id);

        // MapStruct will update only non-null fields
        addressMapper.updateAddressFromDto(addressRequestDTO, existingAddress);

        Address updatedAddress = addressService.save(existingAddress);
        AddressResponseDTO responseDTO = addressMapper.toResponseDto(updatedAddress);

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Get addresses by user ID
     */
    @GetMapping("/user/{userUUID}")
    public ResponseEntity<AddressResponseDTO> getAddressesByUserId(@PathVariable UUID userUUID) {

        Address address = addressService.findByUserUUID(userUUID);
        AddressResponseDTO responseDto = addressMapper.toResponseDto(address);
        responseDto.setUserUUID(userUUID);
        return ResponseEntity.ok(responseDto);
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
