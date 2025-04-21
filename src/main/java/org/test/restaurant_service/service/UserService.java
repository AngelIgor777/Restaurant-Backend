package org.test.restaurant_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.test.restaurant_service.dto.response.UserStaffResponseDTO;
import org.test.restaurant_service.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User save(User user);

    User findByUUID(UUID uuid);

    User findByChatId(Long chatId);

    Page<User> getAll(Pageable pageable);

    List<User> getAllAdminsAndModerators();

    Page<User> search(String query, Pageable pageable);

    List<User> getStaff();
}
