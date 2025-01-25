package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.Address;
import org.test.restaurant_service.repository.AddressRepository;
import org.test.restaurant_service.service.AddressService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    @Override
    public Address findById(Integer id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Address not found with ID: " + id));
    }

    @Override
    public Address save(Address address) {
        return addressRepository.save(address);
    }

    @Override
    public void deleteById(Integer id) {
        if (!addressRepository.existsById(id)) {
            throw new IllegalArgumentException("Address not found with ID: " + id);
        }
        addressRepository.deleteById(id);
    }

    @Override
    public List<Address> findAll() {
        return addressRepository.findAll();
    }

    @Override
    public List<Address> findByCity(String city) {
        return addressRepository.findByCity(city);
    }

    @Override
    public Address findByUserId(Integer userId) {
        return addressRepository.findById(userId)
                .orElseThrow(()-> new EntityNotFoundException("Address not found with ID: " + userId));
    }
}
