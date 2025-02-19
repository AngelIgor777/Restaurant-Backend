package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.test.restaurant_service.entity.Address;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    List<Address> findByCity(String city);

    Optional<Address> findAddressByUser_Uuid(UUID uuid);

}
