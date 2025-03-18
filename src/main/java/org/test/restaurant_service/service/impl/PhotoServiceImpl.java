package org.test.restaurant_service.service.impl;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.test.restaurant_service.dto.request.PhotoRequestDTO;
import org.test.restaurant_service.dto.response.PhotoResponseDTO;
import org.test.restaurant_service.entity.Photo;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.mapper.PhotoMapper;
import org.test.restaurant_service.repository.PhotoRepository;
import org.test.restaurant_service.repository.ProductRepository;
import org.test.restaurant_service.service.PhotoService;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    public static final String IMAGE_DIRECTORY = "uploads/images/";

    protected final PhotoRepository photoRepository;
    protected final ProductRepository productRepository;
    protected final PhotoMapper photoMapper;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void savePhotos(List<Photo> photoRequestDTOS) {
        for (Photo photo : photoRequestDTOS) {
            MultipartFile file = photo.getImage();

            if (file.isEmpty()) {
                throw new BadRequestException("FILE IS EMPTY");
            }
            File directory = new File(IMAGE_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File destFile = new File(IMAGE_DIRECTORY + Objects.requireNonNull(file.getOriginalFilename()).replace(" ", ""));

            try (InputStream inputStream = file.getInputStream();
                 BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destFile))) {

                saveImage(inputStream, outputStream);

            } catch (IOException e) {
                throw new BadRequestException("File could not be saved: " + e.getMessage());
            }
        }
    }

    public void saveImage(InputStream inputStream, BufferedOutputStream outputStream) {
        byte[] buffer = new byte[2048];
        int bytesRead;
        while (true) {
            try {
                if (!((bytesRead = inputStream.read(buffer)) != -1)) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                outputStream.write(buffer, 0, bytesRead);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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

    public void deleteImage(String fileName) {
        File file = new File(IMAGE_DIRECTORY + fileName.replace(" ", ""));
        if (file.exists()) {
            if (!file.delete()) {
                throw new BadRequestException("File could not be deleted: " + fileName);
            }
        } else {
            throw new BadRequestException("File not found: " + fileName);
        }
    }


    @Override
    public PhotoResponseDTO create(Photo photo) {
        Integer productId = photo.getProduct().getId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + productId));
        photo.setUrl(Objects.requireNonNull(photo.getImage().getOriginalFilename()).replace(" ", ""));

        photo.setProduct(product);
        photo = photoRepository.save(photo);
        return photoMapper.toResponseDTO(photo);
    }

    @Override
    public String getContentType(Resource image) {
        String contentType;
        try (InputStream inputStream = image.getInputStream()) {
            contentType = Files.probeContentType(inputStream.available() > 0 ? image.getFile().toPath() : null);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
        } catch (IOException e) {
            try {
                throw new IOException(e);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return contentType;
    }

    @Override
    public Resource getImage(String photoName) {
        Resource resource = new FileSystemResource("uploads/images/" + photoName);

        if (!resource.exists()) {
            try {
                throw new NotFoundException("Resource not found");
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return resource;
    }

    @Override
    public List<PhotoResponseDTO> getPhotosByProductId(Integer productId) {
        return photoRepository.findAllByProductId(productId)
                .stream()
                .map(photoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String getOnePhotoUrl(Integer productId) {
        Optional<PhotoResponseDTO> any = getPhotosByProductId(productId)
                .stream().findAny();
        if (any.isPresent()) {
            return any.get().getUrl();
        } else {
            log.warn("Not exists photo for product with id {}", productId);
        }
        return "";
    }

    @Override
    public PhotoResponseDTO update(Integer id, PhotoRequestDTO requestDTO) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Photo not found with id " + id));
        photoMapper.updateEntityFromRequestDTO(requestDTO, photo);
        if (requestDTO.getProductId() != null) {
            Product product = productRepository.findById(requestDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + requestDTO.getProductId()));
            photo.setProduct(product);
        }
        photo = photoRepository.save(photo);
        return photoMapper.toResponseDTO(photo);
    }

    @Override
    public void delete(Integer id) {
        if (!photoRepository.existsById(id)) {
            throw new EntityNotFoundException("Photo not found with id " + id);
        }
        photoRepository.deleteById(id);
    }
}
