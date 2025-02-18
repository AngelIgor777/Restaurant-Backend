package org.test.restaurant_service.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.entity.Photo;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.mapper.ProductMapper;
import org.test.restaurant_service.service.ProductAndPhotoService;
import org.test.restaurant_service.service.ProductAndProductHistoryService;
import org.test.restaurant_service.service.ProductService;
import org.test.restaurant_service.service.S3Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductAndAndPhotoServiceWithSaveInS3Impl implements ProductAndPhotoService {


    private final ProductAndProductHistoryService productAndProductHistoryService;
    private final S3Service s3Service;
    private final ProductMapper productMapper;
    private final ProductService productService;

    public ProductAndAndPhotoServiceWithSaveInS3Impl(ProductAndProductHistoryService productAndProductHistoryService, S3Service s3Service, ProductMapper productMapper, @Qualifier("productServiceWithS3Impl") ProductService productService) {
        this.productAndProductHistoryService = productAndProductHistoryService;
        this.s3Service = s3Service;
        this.productMapper = productMapper;
        this.productService = productService;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductResponseDTO createProductAndPhotos(Product product, Integer typeId, List<MultipartFile> photoFiles) {
        if (productService.existByName(product.getName())) {
            throw new IllegalArgumentException("Product with name " + product.getName() + " already exists");
        }

        List<Photo> photos = new ArrayList<>();
        for (MultipartFile photoFile : photoFiles) {
            saveFile(product, photoFile, photos);
        }

        product.setPhotos(photos);
        Product createdProduct = productAndProductHistoryService.save(product, typeId);
        return productMapper.toResponseDTO(createdProduct);
    }

    private void saveFile(Product product, MultipartFile photoFile, List<Photo> photos) {
        StringBuilder fileName = new StringBuilder().append(UUID.randomUUID())
                .append(photoFile.getOriginalFilename().substring(photoFile.getOriginalFilename().lastIndexOf(".")));
        Photo photo = Photo.builder()
                .url("https://s3.timeweb.cloud/cf1b889c-51893717-bc35-4427-a93b-2be350132697/uploads/images/" + fileName)
                .product(product)
                .image(photoFile)
                .build();
        s3Service.upload(photoFile, fileName.toString());
        photos.add(photo);
    }

}

