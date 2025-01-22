package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.test.restaurant_service.entity.Address;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    // Custom query to find addresses by city
    List<Address> findByCity(String city);

    // Find all addresses for a specific user
    List<Address> findByUserId(Integer userId);
}
