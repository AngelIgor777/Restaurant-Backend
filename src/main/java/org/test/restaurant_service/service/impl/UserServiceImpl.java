package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.repository.UserRepository;
import org.test.restaurant_service.service.UserService;

import javax.persistence.EntityNotFoundException;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("User with id " + id + " not found"));
    }


}
