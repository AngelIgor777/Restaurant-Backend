package org.test.restaurant_service.service;


import org.springframework.core.io.Resource;
import org.test.restaurant_service.dto.request.PhotoRequestDTO;
import org.test.restaurant_service.dto.response.PhotoResponseDTO;
import org.test.restaurant_service.entity.Photo;

import java.util.List;

public interface PhotoService {

    PhotoResponseDTO create(Photo requestDTO);

    List<PhotoResponseDTO> getPhotosByProductId(Integer productId);

    PhotoResponseDTO update(Integer id, PhotoRequestDTO requestDTO);

    void delete(Integer id);

    void savePhotos(List<Photo> photoRequestDTO);

    String getContentType(Resource image);

    Resource getImage(String photoName);
}
