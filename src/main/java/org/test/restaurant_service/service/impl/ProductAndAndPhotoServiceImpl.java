package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.test.restaurant_service.entity.Photo;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.service.PhotoService;
import org.test.restaurant_service.service.ProductAndPhotoService;
import org.test.restaurant_service.service.ProductService;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductAndAndPhotoServiceImpl implements ProductAndPhotoService {

    private final ProductService productService;
    private final PhotoService photoService;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void createProductAndPhotos(Product product, Integer typeId, List<MultipartFile> photoFiles) {

        List<Photo> photos = new ArrayList<>();
        for (int i = 0; i < photoFiles.size(); i++) {
            Photo photo = Photo.builder()
                    .url(Objects.requireNonNull(photoFiles.get(i).getOriginalFilename()).replace(" ", ""))
                    .product(product)
                    .image(photoFiles.get(i))
                    .build();
            photos.add(photo);
        }
        product.setPhotos(photos);
        productService.create(product, typeId);
        photoService.savePhotos(photos);
    }

}
