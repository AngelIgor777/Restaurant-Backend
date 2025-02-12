package org.test.restaurant_service.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

public interface S3Service {

    CompletableFuture<String>  upload(MultipartFile file, String fileName);
    CompletableFuture<String> upload(String file, String fileName);

    boolean delete(String fileName);
}
