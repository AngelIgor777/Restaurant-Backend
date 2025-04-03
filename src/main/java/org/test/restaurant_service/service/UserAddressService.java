package org.test.restaurant_service.service;

import org.test.restaurant_service.dto.request.AddressRequestDTO;
import org.test.restaurant_service.dto.response.AddressResponseDTO;
import org.test.restaurant_service.entity.Address;
import org.test.restaurant_service.entity.User;

import java.util.UUID;

public interface UserAddressService {

    Address saveAddressToUser(Address address, UUID userUUID);

    Address updateAddressToUser(User user, Address newAddress);

    Address saveAddressToUser(AddressRequestDTO addressRequestDTO, UUID userUUID);
}
