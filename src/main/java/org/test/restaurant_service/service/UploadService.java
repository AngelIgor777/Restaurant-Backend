package org.test.restaurant_service.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploadService {

    String upload(MultipartFile file, String fileName);
}
