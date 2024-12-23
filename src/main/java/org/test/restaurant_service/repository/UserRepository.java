package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.test.restaurant_service.entity.User;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

//    Optional<User> findByChatId(Integer chatId);
}
