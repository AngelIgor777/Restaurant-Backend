package org.test.restaurant_service.service;


import org.springframework.web.multipart.MultipartFile;
import org.test.restaurant_service.dto.request.ProductAndPhotosRequest;
import org.test.restaurant_service.dto.request.ProductRequestDTO;

import java.util.List;

public interface ProductAndPhotoService {
    void createProductAndPhotos(ProductRequestDTO productAndPhotosRequest, List<MultipartFile> file);

}
