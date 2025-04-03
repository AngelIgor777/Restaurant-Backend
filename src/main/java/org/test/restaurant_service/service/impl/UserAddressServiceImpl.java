package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.request.AddressRequestDTO;
import org.test.restaurant_service.entity.Address;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.mapper.AddressMapper;
import org.test.restaurant_service.service.AddressService;
import org.test.restaurant_service.service.UserAddressService;
import org.test.restaurant_service.service.UserService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {

    private final AddressService addressService;
    private final UserService userService;
    private final AddressMapper addressMapper;

    @Override
    public Address saveAddressToUser(Address address, UUID userUUID) {
        User user = userService.findByUUID(userUUID);
        address.setUser(user);
        return addressService.save(address);
    }


    public Address saveAddressToUser(Address address, User user) {
        address.setUser(user);
        return addressService.save(address);
    }

    @Override
    public Address saveAddressToUser(AddressRequestDTO addressRequestDTO, UUID userUUID) {
        Address address = addressMapper.toEntity(addressRequestDTO);
        return saveAddressToUser(address, userUUID);
    }

    @Override
    public Address updateAddressToUser(User user, Address newAddress) {
        Address userAddress = user.getAddress();
        newAddress.setId(userAddress.getId());
        user.setAddress(newAddress);
        return saveAddressToUser(newAddress, user);
    }
}
