package org.test.restaurant_service.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

    String upload(MultipartFile file, String fileName);

    boolean delete(String fileName);
}
