package org.test.restaurant_service.service;

import org.test.restaurant_service.dto.request.AddressRequestDTO;
import org.test.restaurant_service.entity.Address;

import java.util.List;
import java.util.UUID;

public interface AddressService {

    Address findById(Integer id);

    Address save(Address address);

    void deleteById(Integer id);

    List<Address> findAll();

    List<Address> findByCity(String city);

    Address findByUserUUID(UUID userUUID);

    Address updateAddress(Integer id, AddressRequestDTO dto);
}
