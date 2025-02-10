package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.entity.Photo;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.mapper.ProductMapper;
import org.test.restaurant_service.service.PhotoService;
import org.test.restaurant_service.service.ProductAndPhotoService;
import org.test.restaurant_service.service.ProductAndProductHistoryService;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductAndAndPhotoServiceImpl implements ProductAndPhotoService {

    private final PhotoService photoService;
    private final ProductAndProductHistoryService productAndProductHistoryService;
    private final ProductMapper productMapper;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductResponseDTO createProductAndPhotos(Product product, Integer typeId, List<MultipartFile> photoFiles) {

        List<Photo> photos = new ArrayList<>();
        for (MultipartFile photoFile : photoFiles) {
            Photo photo = Photo.builder()
                    .url(Objects.requireNonNull(photoFile.getOriginalFilename()).replace(" ", ""))
                    .product(product)
                    .image(photoFile)
                    .build();
            photos.add(photo);
        }
        product.setPhotos(photos);
        Product createdProduct = productAndProductHistoryService.save(product, typeId);


        photoService.savePhotos(photos);
        return   productMapper.toResponseDTO(createdProduct);
    }

}
