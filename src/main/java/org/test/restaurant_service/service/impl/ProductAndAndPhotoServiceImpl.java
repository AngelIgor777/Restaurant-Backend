package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.test.restaurant_service.dto.request.PhotoRequestDTO;
import org.test.restaurant_service.dto.request.ProductRequestDTO;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.service.PhotoService;
import org.test.restaurant_service.service.ProductAndPhotoService;
import org.test.restaurant_service.service.ProductService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductAndAndPhotoServiceImpl implements ProductAndPhotoService {

    private final ProductService productService;
    private final PhotoService photoService;

    @Override
    public void createProductAndPhotos(ProductRequestDTO productRequestDTO, List<MultipartFile> photos) {
        ProductResponseDTO productResponseDTO = productService.create(productRequestDTO);

        List<PhotoRequestDTO> photoRequestDTOS = new ArrayList<>();
        for (int i = 0; i < photos.size(); i++) {
            PhotoRequestDTO photoRequestDTO = new PhotoRequestDTO(productResponseDTO.getId(), photos.get(i));
            photoRequestDTOS.add(photoRequestDTO);
        }

        photoService.savePhotos(photoRequestDTOS);
    }

}
