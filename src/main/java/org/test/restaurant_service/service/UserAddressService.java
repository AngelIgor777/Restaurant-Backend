package org.test.restaurant_service.service;

import org.test.restaurant_service.dto.response.AddressResponseDTO;
import org.test.restaurant_service.entity.Address;

import java.util.UUID;

public interface UserAddressService {

    Address saveAddressToUser(Address address, UUID userUUID);
}
