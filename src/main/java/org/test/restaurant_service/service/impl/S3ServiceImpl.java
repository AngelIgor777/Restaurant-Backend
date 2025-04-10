package org.test.restaurant_service.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.test.restaurant_service.service.S3Service;
import org.test.restaurant_service.util.KeyUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private static final String IMAGE_MIME_TYPE_PREFIX = "image/";
    private static final String IMAGE_UPLOAD_DIR = "uploads/images/";
    private static final int MAX_IMAGE_WIDTH = 800;

    private final AmazonS3 amazonS3;

    @Override
    public String upload(MultipartFile file, String fileName) {
        return uploadInternal(fileName, () -> optimizeImage(file.getBytes(), file.getOriginalFilename()));
    }

    @Override
    public String upload(String fileUrl, String fileName) {
        return uploadInternal(fileName, () -> {
            try (InputStream inputStream = new URL(fileUrl).openStream()) {
                return optimizeImage(inputStream.readAllBytes(), fileUrl);
            }
        });
    }

    private String uploadInternal(String fileName, FileContentProvider contentProvider) {
        fileName = normalizeFileName(fileName);
        String filePath = IMAGE_UPLOAD_DIR + fileName;

        try {
            byte[] optimizedImage = contentProvider.getContent();
            String contentType = determineContentType(fileName);

            if (contentType == null) {
                log.warn("Unsupported file type for file: {}", fileName);
                return null;
            }

            saveToS3(filePath, contentType, optimizedImage);
            return fileName;
        } catch (IOException e) {
            log.error("Error uploading file: {}", fileName, e);
            return null;
        }
    }

    private void saveToS3(String filePath, String contentType, byte[] fileData) {
        try (InputStream inputStream = new ByteArrayInputStream(fileData)) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(fileData.length);
            metadata.setContentDisposition("inline");

            TransferManager transferManager = TransferManagerBuilder.standard()
                    .withS3Client(amazonS3)
                    .withMultipartUploadThreshold(10L * 1024 * 1024) // 10MB
                    .build();

            PutObjectRequest request = new PutObjectRequest(KeyUtil.getBucketName(), filePath, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            transferManager.upload(request);
        } catch (IOException e) {
            log.error("Error saving file to S3: {}", filePath, e);
        }
    }

    private byte[] optimizeImage(byte[] imageBytes, String sourceName) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        if (image == null) {
            throw new IOException("Unable to read image from source: " + sourceName);
        }

        int newHeight = (image.getHeight() * MAX_IMAGE_WIDTH) / image.getWidth();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Thumbnails.of(image)
                    .size(MAX_IMAGE_WIDTH, newHeight)
                    .outputQuality(0.85)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);
            return outputStream.toByteArray();
        }
    }

    @Override
    public boolean delete(String fileS3Path) {
        String filePath = extractUploadPath(fileS3Path);
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(KeyUtil.getBucketName(), filePath));
            return true;
        } catch (Exception e) {
            log.error("Error deleting file: {}", fileS3Path, e);
            return false;
        }
    }

    private String normalizeFileName(String fileName) {
        return fileName.startsWith("/") ? fileName.substring(1) : fileName;
    }

    private String extractUploadPath(String fullPath) {
        int index = fullPath.indexOf(IMAGE_UPLOAD_DIR);
        return (index != -1) ? fullPath.substring(index) : fullPath;
    }

    private String determineContentType(String filename) {
        if (filename == null) {
            return null;
        }
        String lowerCase = filename.toLowerCase();
        if (lowerCase.endsWith(".jpg") || lowerCase.endsWith(".jpeg")) {
            return IMAGE_MIME_TYPE_PREFIX + "jpeg";
        } else if (lowerCase.endsWith(".png")) {
            return IMAGE_MIME_TYPE_PREFIX + "png";
        }
        return null;
    }

    @FunctionalInterface
    private interface FileContentProvider {
        byte[] getContent() throws IOException;
    }
}
