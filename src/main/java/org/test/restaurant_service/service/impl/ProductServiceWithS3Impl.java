package org.test.restaurant_service.service.impl;

import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.Photo;
import org.test.restaurant_service.mapper.PhotoMapper;
import org.test.restaurant_service.mapper.ProductMapper;
import org.test.restaurant_service.repository.PhotoRepository;
import org.test.restaurant_service.repository.ProductRepository;
import org.test.restaurant_service.repository.ProductTypeRepository;
import org.test.restaurant_service.service.S3Service;

import java.util.List;

@Service
public class ProductServiceWithS3Impl extends ProductServiceImpl{

    private final S3Service s3Service;
    public ProductServiceWithS3Impl(ProductRepository productRepository, ProductTypeRepository productTypeRepository, ProductMapper productMapper, PhotoMapper photoMapper, PhotoRepository photoRepository, PhotoServiceImpl photoServiceImpl, S3Service s3Service) {
        super(productRepository, productTypeRepository, productMapper, photoMapper, photoRepository, photoServiceImpl);
        this.s3Service = s3Service;
    }

    @Override
    public void delete(Integer id) {
        List<Photo> photoList = photoRepository.findAllByProductId(id);
        for (Photo photo : photoList) {
            s3Service.delete(photo.getUrl());
        }
        productRepository.deleteById(id);
        photoRepository.deleteAll(photoList);

    }
}
