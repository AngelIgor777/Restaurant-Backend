package org.test.restaurant_service.service;

import org.springframework.web.multipart.MultipartFile;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.entity.Product;
import java.util.List;

public interface ProductAndPhotoService {
    ProductResponseDTO createProductAndPhotos(Product product, Integer typeId, List<MultipartFile> photoFiles);
}
