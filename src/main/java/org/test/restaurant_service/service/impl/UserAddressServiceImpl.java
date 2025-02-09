package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.Address;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.service.AddressService;
import org.test.restaurant_service.service.UserAddressService;
import org.test.restaurant_service.service.UserService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {

    private final AddressService addressService;
    private final UserService userService;

    @Override
    public Address saveAddressToUser(Address address, UUID userUUID) {
        User user = userService.findByUUID(userUUID);
        address.setUser(user);
        return addressService.save(address);
    }
}
