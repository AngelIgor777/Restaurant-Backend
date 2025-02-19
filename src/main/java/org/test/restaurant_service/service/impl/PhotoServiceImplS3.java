package org.test.restaurant_service.service.impl;

import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.Photo;
import org.test.restaurant_service.mapper.PhotoMapper;
import org.test.restaurant_service.repository.PhotoRepository;
import org.test.restaurant_service.repository.ProductRepository;
import org.test.restaurant_service.service.S3Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class PhotoServiceImplS3 extends PhotoServiceImpl {

    private final S3Service s3Service;

    public PhotoServiceImplS3(PhotoRepository photoRepository, ProductRepository productRepository, PhotoMapper photoMapper, S3Service s3Service) {
        super(photoRepository, productRepository, photoMapper);
        this.s3Service = s3Service;
    }

    @Override
    public void savePhotos(List<Photo> photos) {
        for (Photo photo : photos) {
            photoRepository.save(photo);
            s3Service.upload(photo.getImage(), photo.getUrl().substring(photo.getUrl().lastIndexOf("uploads/images")));
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deletePhotos(List<String> fileNames) {
        for (String fileName : fileNames) {
            deleteImage(fileName);

            Optional<Photo> photo = photoRepository.findByUrl(fileName);
            photo.ifPresent(photoRepository::delete);
        }
    }


    @Override
    public void deleteImage(String fileName) {
        s3Service.delete(fileName);
    }
}
