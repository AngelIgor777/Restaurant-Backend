package org.test.restaurant_service.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.test.restaurant_service.service.S3Service;
import org.test.restaurant_service.util.KeyUtil;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final TransferManager transferManager;

    private final AmazonS3 amazonS3;

    // Supported image MIME types
    private static final String IMAGE_MIME_TYPE_PREFIX = "image/";

    @Override
    public String upload(MultipartFile file, String fileName) {
        String filePath = "uploads/images/" + fileName;

        // Automatically set content type based on file extension
        String contentType = determineContentType(file.getOriginalFilename());

        if (contentType == null) {
            log.error("Unsupported file type for file: {}", file.getOriginalFilename());
            return null;
        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(file.getSize());

        try (InputStream inputStream = file.getInputStream()) {
            Upload upload = transferManager.upload(new PutObjectRequest(
                    KeyUtil.getBucketName(),
                    filePath,
                    inputStream,
                    metadata)
            );
            upload.waitForCompletion();
        } catch (InterruptedException | IOException e) {
            log.error("Error uploading file", e);
        }
        return file.getOriginalFilename();
    }

    @Override
    public boolean delete(String fileS3Path) {
        String filePath = fileS3Path.substring(fileS3Path.indexOf("uploads/"));

        try {
            // Create a delete request for the file
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(KeyUtil.getBucketName(), filePath);

            // Perform the delete operation
            amazonS3.deleteObject(deleteObjectRequest);
            return true;
        } catch (Exception e) {
            log.error("Error deleting file: {}", fileS3Path, e);
            return false;
        }
    }

    /**
     * Determines the MIME type based on file extension
     *
     * @param filename The name of the file
     * @return The MIME type for image files, or null if not supported
     */
    private String determineContentType(String filename) {
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return IMAGE_MIME_TYPE_PREFIX + "jpeg";
        } else if (filename.endsWith(".png")) {
            return IMAGE_MIME_TYPE_PREFIX + "png";
        }
        // Add more formats as needed
        return null; // Unsupported type
    }
}
