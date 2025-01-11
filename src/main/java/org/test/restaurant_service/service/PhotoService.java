package org.test.restaurant_service.service;



import org.test.restaurant_service.dto.request.PhotoRequestDTO;
import org.test.restaurant_service.dto.response.PhotoResponseDTO;

import java.util.List;

public interface PhotoService {

    PhotoResponseDTO create(PhotoRequestDTO requestDTO);

    List<PhotoResponseDTO> getPhotosByProductId(Integer productId);

    PhotoResponseDTO update(Integer id, PhotoRequestDTO requestDTO);

    void delete(Integer id);

    void savePhotos(List<PhotoRequestDTO> photoRequestDTO);
}
