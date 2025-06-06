package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.request.AddressRequestDTO;
import org.test.restaurant_service.dto.response.AddressResponseDTO;
import org.test.restaurant_service.entity.Address;
import org.test.restaurant_service.mapper.AddressMapper;
import org.test.restaurant_service.service.AddressService;
import org.test.restaurant_service.service.UserAddressService;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final AddressService addressService;
    private final AddressMapper addressMapper;
    private final UserAddressService userAddressService;

    @PatchMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> updateAddress(
            @PathVariable Integer id,
            @RequestBody @Valid AddressRequestDTO dto
    ) {
        Address updated = addressService.updateAddress(id, dto);
        return ResponseEntity.ok(addressMapper.toResponseDto(updated));
    }

    @GetMapping("/user/{userUUID}")
    @PreAuthorize("@securityService.userIsOwnerOrModeratorOrAdmin(authentication, #userUUID)")
    public ResponseEntity<AddressResponseDTO> getAddressesByUserId(@PathVariable UUID userUUID) {

        Address address = addressService.findByUserUUID(userUUID);
        AddressResponseDTO responseDto = addressMapper.toResponseDto(address);
        responseDto.setUserUUID(userUUID);
        return ResponseEntity.ok(responseDto);
    }


    @PostMapping
    //todo check user save own address or not
    public ResponseEntity<AddressResponseDTO> saveAddress(@RequestBody AddressRequestDTO addressRequestDTO) {
        Address address = addressMapper.toEntity(addressRequestDTO);
        Address savedAddress = userAddressService.saveAddressToUser(address, addressRequestDTO.getUserUUID());

        AddressResponseDTO responseDto = addressMapper.toResponseDto(savedAddress);

        responseDto.setUserUUID(savedAddress.getUser().getUuid());
        return ResponseEntity.ok(responseDto);
    }

}
