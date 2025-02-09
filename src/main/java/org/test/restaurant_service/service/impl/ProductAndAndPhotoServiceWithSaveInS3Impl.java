package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.test.restaurant_service.entity.Photo;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.service.ProductAndPhotoService;
import org.test.restaurant_service.service.ProductAndProductHistoryService;
import org.test.restaurant_service.service.UploadService;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductAndAndPhotoServiceWithSaveInS3Impl implements ProductAndPhotoService {


    private final ProductAndProductHistoryService productAndProductHistoryService;
    private final UploadService uploadService;

    @Override
    @Transactional(rollbackOn = Exception.class)

    public void createProductAndPhotos(Product product, Integer typeId, List<MultipartFile> photoFiles) {
        List<Photo> photos = new ArrayList<>();
        for (MultipartFile photoFile : photoFiles) {
            String fileName = Objects.requireNonNull(photoFile.getOriginalFilename()).replace(" ", "");
            Photo photo = Photo.builder()
                    .url("https://s3.timeweb.cloud/cf1b889c-51893717-bc35-4427-a93b-2be350132697/uploads/images/" + fileName)
                    .product(product)
                    .image(photoFile)
                    .build();
            photos.add(photo);
            uploadService.upload(photoFile, fileName);
        }
        product.setPhotos(photos);
        productAndProductHistoryService.save(product, typeId);

    }

}

