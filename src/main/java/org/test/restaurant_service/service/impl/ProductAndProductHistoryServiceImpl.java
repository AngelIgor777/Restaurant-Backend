package org.test.restaurant_service.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.test.restaurant_service.entity.Photo;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.service.*;
import org.test.restaurant_service.util.KeyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ProductAndProductHistoryServiceImpl implements ProductAndProductHistoryService {

    private final ProductService productService;
    private final ProductHistoryService productHistoryService;
    private final S3Service s3Service;

    public ProductAndProductHistoryServiceImpl(@Qualifier("productServiceImpl") ProductService productService, ProductHistoryService productHistoryService, S3Service s3Service) {
        this.productService = productService;
        this.productHistoryService = productHistoryService;
        this.s3Service = s3Service;
    }


    @Override
    public Product save(Product product, Integer typeId) {
        Product createdProduct = productService.create(product, typeId);
        productHistoryService.saveToProductHistory(createdProduct);
        return createdProduct;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Product update(Product product, Integer id, MultipartFile photoFile) {
        List<Photo> photos = null;
        if (photoFile != null) {
            StringBuilder fileName = new StringBuilder().append(UUID.randomUUID())
                    .append(Objects.requireNonNull(photoFile.getOriginalFilename()).substring(photoFile.getOriginalFilename().lastIndexOf(".")));


            photos = new ArrayList<>();
            photos.add(Photo.builder()
                    .product(product)
                    .url(KeyUtil.getS3URL() + "/" + KeyUtil.getBucketName() + "/uploads/images/" + fileName)
                    .image(photoFile)
                    .build());
        }

        Product updatedProduct = productService.update(product, id, photos);

        if (photos != null) {
            for (Photo photo : photos) {
                photo.setProduct(updatedProduct);
                String photoName = photo.getUrl().substring(photo.getUrl().lastIndexOf("/"));
                s3Service.upload(photo.getImage(), photoName);
            }
        }


        productHistoryService.saveToProductHistory(updatedProduct);
        return updatedProduct;
    }

}
